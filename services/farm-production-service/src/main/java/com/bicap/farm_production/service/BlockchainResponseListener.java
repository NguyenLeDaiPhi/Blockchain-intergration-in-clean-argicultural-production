package com.bicap.farm_production.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bicap.farm_production.dto.BlockchainResult;
import com.bicap.farm_production.entity.FarmingProcess;
import com.bicap.farm_production.entity.FarmingSeason;
import com.bicap.farm_production.entity.SeasonStatus;
import com.bicap.farm_production.repository.FarmingProcessRepository;
import com.bicap.farm_production.repository.FarmingSeasonRepository;
import com.bicap.farm_production.util.BlockchainConstants;

@Service
public class BlockchainResponseListener {

    @Autowired
    private FarmingSeasonRepository seasonRepository;

    @Autowired
    private FarmingProcessRepository processRepository;

    @RabbitListener(queues = "${bicap.rabbitmq.queue.response}")
    @Transactional
    public void handleBlockchainResult(BlockchainResult result) {
        System.out.println("Received Result: " + result);

        try {
            String type = result.getResourceType();
            Long id = Long.parseLong(result.getResourceId());

            // SỬ DỤNG CONSTANTS ĐỂ KIỂM TRA LOẠI DỮ LIỆU
            if (BlockchainConstants.TYPE_SEASON.equalsIgnoreCase(type)) {
                updateSeasonStatus(id, result);
            } else if (BlockchainConstants.TYPE_PROCESS.equalsIgnoreCase(type)) {
                updateProcessStatus(id, result);
            } else {
                System.out.println("Unknown Resource Type: " + type);
            }

        } catch (Exception e) {
            System.err.println("Error processing blockchain result: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Logic cập nhật cho Mùa Vụ
    private void updateSeasonStatus(Long id, BlockchainResult result) {
        FarmingSeason season = seasonRepository.findById(id).orElse(null);
        if (season == null) {
            System.err.println("Season not found with ID: " + id);
            return;
        }

        if (result.isSuccess()) {
            season.setBlockchainTxId(result.getTransactionId());
            season.setStatus(SeasonStatus.SYNCED_BLOCKCHAIN);
            System.out.println("Season " + id + " synced successfully. Tx: " + result.getTransactionId());
        } else {
            season.setStatus(SeasonStatus.FAILED);
            System.err.println("Season " + id + " sync failed: " + result.getErrorMessage());
        }
        seasonRepository.save(season);
    }

    // Logic cập nhật cho Nhật ký canh tác
    private void updateProcessStatus(Long id, BlockchainResult result) {
        FarmingProcess process = processRepository.findById(id).orElse(null);
        if (process == null) {
            System.err.println("Process not found with ID: " + id);
            return;
        }

        if (result.isSuccess()) {
            process.setBlockchainTxId(result.getTransactionId());
            process.setSyncStatus(SeasonStatus.SYNCED_BLOCKCHAIN);
            System.out.println("Process " + id + " synced successfully. Tx: " + result.getTransactionId());
        } else {
            process.setSyncStatus(SeasonStatus.FAILED);
            System.err.println("Process " + id + " sync failed: " + result.getErrorMessage());
        }
        processRepository.save(process);
    }
}