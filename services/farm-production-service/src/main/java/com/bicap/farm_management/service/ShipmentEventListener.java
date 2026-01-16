package com.bicap.farm_management.service;

import com.bicap.farm_management.config.RabbitMQConfig;
import com.bicap.farm_management.dto.ShipmentEvent;
import com.bicap.farm_management.entity.PurchaseOrder;
import com.bicap.farm_management.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentEventListener.class);
    private final PurchaseOrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.SHIPMENT_QUEUE)
    public void handleShipmentUpdate(ShipmentEvent event) {
        logger.info("Farm Service nhận tin nhắn: {}", event);

        if ("DELIVERED".equals(event.getStatus())) {
            orderRepository.findById(event.getOrderId()).ifPresentOrElse(
                order -> {
                    order.setStatus("COMPLETED");
                    orderRepository.save(order);
                    logger.info("Đã cập nhật đơn hàng {} -> COMPLETED", event.getOrderId());
                },
                () -> logger.warn("Không tìm thấy đơn hàng ID: {}", event.getOrderId())
            );
        }
    }
}