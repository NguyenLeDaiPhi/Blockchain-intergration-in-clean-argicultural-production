package com.example.logistic_service.rabbitmq;

import com.example.logistic_service.config.RabbitMQConfig;
import com.example.logistic_service.dto.ShipmentStatusEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendStatusUpdate(ShipmentStatusEvent event) {
        // Gửi tin nhắn vào Exchange với Routing Key
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LOGISTICS_EXCHANGE,
                "shipment.status.update",
                event
        );
        System.out.println("Đã gửi thông báo trạng thái cho Shipment ID: " + event.getShipmentId());
    }
}