package com.bicap.farm_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShipmentEventListener {

    private final OrderService orderService;

    @RabbitListener(queues = "shipment.status.queue")
    public void handleShipmentStatusUpdate(Map<String, Object> message) {
        try {
            Long orderId = ((Number) message.get("orderId")).longValue();
            String status = (String) message.get("status");
            
            System.out.println("Received shipment update for Order ID: " + orderId + ", Status: " + status);

            orderService.updateOrderStatusFromShipment(orderId, status);
            
        } catch (Exception e) {
            System.err.println("Error processing shipment status update: " + e.getMessage());
        }
    }
}