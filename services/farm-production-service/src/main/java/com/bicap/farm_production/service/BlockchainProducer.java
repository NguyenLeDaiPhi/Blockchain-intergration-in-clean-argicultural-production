package com.bicap.farm_production.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bicap.farm_production.dto.BlockchainMessage;

@Service
public class BlockchainProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${bicap.rabbitmq.exchange}")
    private String exchange;

    @Value("${bicap.rabbitmq.routing-key.request}")
    private String routingKey;

    public void sendToBlockchain(String id, String dataHash) {
        BlockchainMessage message = new BlockchainMessage(
            id, 
            "SEASON", 
            dataHash, 
            "CREATE"
        );
        
        System.out.println("Sending message to Queue: " + message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}