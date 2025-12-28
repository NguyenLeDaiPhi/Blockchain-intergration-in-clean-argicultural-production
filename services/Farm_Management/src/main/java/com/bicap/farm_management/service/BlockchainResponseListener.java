package com.bicap.farm_management.service;

import com.bicap.farm_management.dto.BlockchainResult;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockchainResponseListener {

    @Autowired
    private ProductionBatchService batchService;

    // Lắng nghe hàng đợi phản hồi (Lấy tên từ application.properties)
    @RabbitListener(queues = "${bicap.rabbitmq.queue.response}")
    public void receiveBlockchainResponse(BlockchainResult result) {
        System.out.println("📩 [RECV] Nhận phản hồi từ Blockchain: " + result);

        if (result.isSuccess()) {
            // Nếu là loại BATCH thì cập nhật bảng ProductionBatch
            if ("BATCH".equals(result.getResourceType())) {
                Long batchId = Long.valueOf(result.getResourceId());
                batchService.updateBlockchainStatus(batchId, result.getTransactionId());
            }
        } else {
            System.err.println("❌ Blockchain báo lỗi: " + result.getErrorMessage());
        }
    }
}