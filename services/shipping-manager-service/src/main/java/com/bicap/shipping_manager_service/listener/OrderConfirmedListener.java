package com.bicap.shipping_manager_service.listener;

import com.bicap.shipping_manager_service.entity.Shipment;
import com.bicap.shipping_manager_service.entity.ShipmentStatus;
import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * RabbitMQ Listener để nhận OrderConfirmedEvent từ Trading Order Service
 * Khi order được confirm, tự động tạo shipment record cho shipping manager
 */
@Slf4j
@Component
public class OrderConfirmedListener {

    private final ShipmentRepository shipmentRepository;

    public OrderConfirmedListener(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * Listener để nhận order confirmed event từ Trading Order Service
     * @param message OrderConfirmedEvent data
     */
    @RabbitListener(queues = "${bicap.rabbitmq.queue.order.confirmed:bicap.order.confirmed.queue}")
    @Transactional
    public void receiveOrderConfirmed(Map<String, Object> message) {
        try {
            Long orderId = getLongValue(message, "orderId");
            String buyerEmail = (String) message.get("buyerEmail");
            String farmManagerEmail = (String) message.get("farmManagerEmail");
            Long farmId = getLongValue(message, "farmId");
            Double totalAmount = getDoubleValue(message, "totalAmount");
            String shippingAddress = (String) message.get("shippingAddress");

            log.info("✅ [RabbitMQ] Received order confirmed event - Order ID: {}, Farm ID: {}, Buyer: {}", 
                    orderId, farmId, buyerEmail);

            // Kiểm tra xem đã có shipment cho order này chưa
            boolean exists = shipmentRepository.findByOrderId(orderId).isPresent();
            if (exists) {
                log.info("⚠️ Shipment already exists for order ID: {}", orderId);
                return;
            }

            // Tạo shipment mới với status PENDING
            // Note: Shipment entity chỉ có orderId và status, các thông tin khác (buyerEmail, shippingAddress, etc.)
            // sẽ được lấy từ order service khi hiển thị trong UI
            Shipment shipment = new Shipment();
            shipment.setOrderId(orderId);
            shipment.setStatus(ShipmentStatus.PENDING);
            // fromLocation và toLocation sẽ được set khi shipping manager tạo shipment detail
            shipmentRepository.save(shipment);
            log.info("✅ [RabbitMQ] Created shipment for order ID: {} - Order will appear in shipping manager orders page", orderId);

        } catch (Exception e) {
            log.error("❌ Error processing order confirmed event: {}", e.getMessage(), e);
        }
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
