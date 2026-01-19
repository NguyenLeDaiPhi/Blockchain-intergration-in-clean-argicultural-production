package com.bicap.farm_management.service;

import com.bicap.farm_management.dto.BlockchainMessage;
import com.bicap.farm_management.entity.ExportBatch;
import com.bicap.farm_management.entity.FarmingProcess;
import com.bicap.farm_management.entity.ProductionBatch;
import com.bicap.farm_management.repository.ExportBatchRepository;
import com.bicap.farm_management.repository.FarmingProcessRepository;
import com.bicap.farm_management.repository.ProductionBatchRepository;
import com.bicap.farm_management.util.QRCodeGenerator;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SeasonService {

    @Autowired private ProductionBatchRepository batchRepo;
    @Autowired private FarmingProcessRepository processRepo;
    @Autowired private ExportBatchRepository exportRepo;
    @Autowired private RabbitTemplate rabbitTemplate;

    @Value("${bicap.blockchain.exchange}")
    private String blockchainExchange;

    @Value("${bicap.blockchain.routing_key}")
    private String blockchainRoutingKey;

    // 1. Tạo mùa vụ mới & Gửi Blockchain
    @Transactional
    public ProductionBatch createSeason(ProductionBatch batch) {
        batch.setCreatedAt(LocalDateTime.now());
        batch.setStatus("ACTIVE");
        ProductionBatch saved = batchRepo.save(batch);

        // Gửi thông tin sang Blockchain Service
        sendToBlockchain(saved.getId().toString(), "BATCH", "Created Batch: " + saved.getBatchCode());
        
        return saved;
    }

    // 2. Cập nhật tiến trình (Nhật ký) & Gửi Blockchain
    @Transactional
    public FarmingProcess addProcess(Long batchId, FarmingProcess process) {
        ProductionBatch batch = batchRepo.findById(batchId).orElseThrow();
        process.setProductionBatch(batch);
        process.setPerformedDate(LocalDateTime.now());
        
        FarmingProcess saved = processRepo.save(process);

        // Gửi thông tin sang Blockchain Service
        sendToBlockchain(saved.getId().toString(), "PROCESS", 
            "Batch " + batch.getBatchCode() + " - " + process.getProcessType() + ": " + process.getDescription());

        return saved;
    }

    // 3. Xuất tiến trình (Tạo đợt xuất hàng) & Tạo QR & Gửi Blockchain
    @Transactional
    public ExportBatch exportSeason(Long batchId, ExportBatch exportInfo) {
        ProductionBatch batch = batchRepo.findById(batchId).orElseThrow();
        
        exportInfo.setProductionBatch(batch);
        exportInfo.setExportDate(LocalDateTime.now());
        // Tạo mã export code nếu chưa có
        if(exportInfo.getBatchCode() == null) {
            exportInfo.setBatchCode("EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // --- TẠO QR CODE ---
        try {
            // URL dẫn đến trang truy xuất nguồn gốc (Public)
            String traceUrl = "http://localhost:3000/trace/" + exportInfo.getBatchCode();
            exportInfo.setQrCodeUrl(traceUrl);
            
            // Tạo ảnh QR Base64
            String qrImage = QRCodeGenerator.generateQRCodeImage(traceUrl, 300, 300);
            exportInfo.setQrCodeImage(qrImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExportBatch saved = exportRepo.save(exportInfo);

        // Gửi thông tin sang Blockchain Service
        sendToBlockchain(saved.getId().toString(), "EXPORT", 
            "Export " + saved.getBatchCode() + " from Batch " + batch.getBatchCode());

        return saved;
    }

    public List<FarmingProcess> getSeasonProcesses(Long batchId) {
        return processRepo.findByProductionBatchId(batchId);
    }

    public ProductionBatch getSeasonDetails(Long batchId) {
        return batchRepo.findById(batchId).orElse(null);
    }

    private void sendToBlockchain(String resourceId, String type, String data) {
        try {
            // Giả lập hash dữ liệu đơn giản (Thực tế có thể hash SHA-256 object JSON)
            String dataHash = Integer.toHexString(data.hashCode());
            
            BlockchainMessage msg = new BlockchainMessage(resourceId, type, dataHash, LocalDateTime.now().toString());
            rabbitTemplate.convertAndSend(blockchainExchange, blockchainRoutingKey, msg);
        } catch (Exception e) {
            System.err.println("Lỗi gửi Blockchain: " + e.getMessage());
        }
    }
}