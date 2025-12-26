package com.example.logistic_service.rabbitmq;

import com.example.logistic_service.config.RabbitMQConfig;
import com.example.logistic_service.dto.OrderPlacedEvent;
import com.example.logistic_service.services.ShipmentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private ShipmentService shipmentService;

    // Lắng nghe queue khi có đơn hàng mới từ Trading Service
    @RabbitListener(queues = RabbitMQConfig.LOGISTICS_QUEUE)
    public void receiveOrderEvent(OrderPlacedEvent event) {
        System.out.println("Nhận đơn hàng mới: " + event.getOrderId());
        shipmentService.createShipment(event);
    }
}