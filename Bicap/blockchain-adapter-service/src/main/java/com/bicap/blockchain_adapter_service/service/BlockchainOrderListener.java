package com.bicap.blockchain_adapter_service.service;

import com.bicap.blockchain_adapter_service.config.RabbitMQConfig;
import com.bicap.blockchain_adapter_service.dto.OrderCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BlockchainOrderListener {

    @RabbitListener(queues = RabbitMQConfig.BLOCKCHAIN_QUEUE)
    public void handleOrderCompleted(OrderCompletedEvent event) {

        System.out.println(" Blockchain Adapter received order.completed");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Buyer ID: " + event.getBuyerId());
        System.out.println("Total: " + event.getTotalAmount());
        System.out.println("Completed At: " + event.getCompletedAt());

        // MOCK ghi blockchain
        System.out.println(" Blockchain write simulated");
    }
}
