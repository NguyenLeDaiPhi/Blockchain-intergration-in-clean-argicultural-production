package com.bicap.farm_production.service;


import com.bicap.farm_production.dto.BlockchainResult;
import com.bicap.farm_production.entity.FarmingSeason;
import com.bicap.farm_production.entity.SeasonStatus; // Enum: PENDING, SYNCED, FAILED
import com.bicap.farm_production.repository.FarmingSeasonRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlockchainResponseListener {

    @Autowired
    private FarmingSeasonRepository seasonRepository;

    // Lắng nghe queue response định nghĩa trong application.properties
    @RabbitListener(queues = "${bicap.rabbitmq.queue.response}")
    @Transactional
    public void handleBlockchainResult(BlockchainResult result) {
        System.out.println("Received Result from Blockchain Adapter: " + result);

        try {
            Long seasonId = Long.parseLong(result.getResourceId());
            FarmingSeason season = seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new RuntimeException("Season not found: " + seasonId));

            if (result.isSuccess()) {
                season.setBlockchainTxId(result.getTransactionId());
                season.setStatus(SeasonStatus.SYNCED_BLOCKCHAIN);
            } else {
                season.setStatus(SeasonStatus.FAILED);
                // Có thể log error message vào DB để admin kiểm tra
                System.err.println("Blockchain Error: " + result.getErrorMessage());
            }

            seasonRepository.save(season);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
