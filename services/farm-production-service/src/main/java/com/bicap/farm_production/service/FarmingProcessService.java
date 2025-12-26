package com.bicap.farm_production.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bicap.farm_production.entity.FarmingProcess;
import com.bicap.farm_production.entity.FarmingSeason;
import com.bicap.farm_production.entity.SeasonStatus;
import com.bicap.farm_production.repository.FarmingProcessRepository;
import com.bicap.farm_production.repository.FarmingSeasonRepository;
import com.bicap.farm_production.util.BlockchainConstants;

@Service
public class FarmingProcessService {

    @Autowired
    private FarmingProcessRepository processRepository;

    @Autowired
    private FarmingSeasonRepository seasonRepository;

    @Autowired
    private BlockchainProducer blockchainProducer;

    @Transactional
    public FarmingProcess addProcess(Long seasonId, FarmingProcess process) {
        // 1. Kiểm tra Season có tồn tại không
        FarmingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found with id: " + seasonId));

        process.setSeason(season);
        process.setPerformedDate(LocalDateTime.now());
        process.setSyncStatus(SeasonStatus.PLANNING); 

        // 2. Lưu tạm để lấy ID
        FarmingProcess savedProcess = processRepository.save(process);

        // 3. Tính Hash (ID + Activity + Date)
        String rawData = savedProcess.getId() + savedProcess.getActivityType() + savedProcess.getPerformedDate().toString();
        String dataHash = calculateSHA256(rawData);

        // 4. Cập nhật Hash & Trạng thái
        savedProcess.setDataHash(dataHash);
        savedProcess.setSyncStatus(SeasonStatus.PENDING_BLOCKCHAIN);
        processRepository.save(savedProcess);

        // 5. Gửi sang Blockchain Adapter (Sử dụng Constant TYPE_PROCESS)
        blockchainProducer.sendToBlockchain(
            savedProcess.getId().toString(), 
            dataHash, 
            BlockchainConstants.TYPE_PROCESS
        );

        return savedProcess;
    }

    public List<FarmingProcess> getProcessesBySeason(Long seasonId) {
        return processRepository.findBySeasonId(seasonId);
    }
    
    // Hàm băm helper
    private String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }
}