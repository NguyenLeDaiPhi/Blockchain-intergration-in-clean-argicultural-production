package com.bicap.farm_production.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bicap.farm_production.dto.BlockchainMessage;
import com.bicap.farm_production.util.BlockchainConstants;

@Service
public class BlockchainProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${bicap.rabbitmq.exchange}")
    private String exchange;

    @Value("${bicap.rabbitmq.routing-key.request}")
    private String routingKey;

    /**
     * Gửi yêu cầu ghi dữ liệu lên Blockchain
     * @param id ID của đối tượng (Season ID hoặc Process ID)
     * @param dataHash Hash SHA-256 của dữ liệu
     * @param objectType Loại đối tượng (Lấy từ BlockchainConstants)
     */
    public void sendToBlockchain(String id, String dataHash, String objectType) {
        BlockchainMessage message = new BlockchainMessage(
            id, 
            objectType, 
            dataHash, 
            BlockchainConstants.ACTION_CREATE // Sử dụng Constant thay vì hardcode "CREATE"
        );
        
        System.out.println("Sending " + objectType + " to Queue: " + message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}