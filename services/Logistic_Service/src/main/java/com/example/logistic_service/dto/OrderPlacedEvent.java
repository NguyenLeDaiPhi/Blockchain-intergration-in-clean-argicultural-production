package com.example.logistic_service.dto;
import lombok.Data;

@Data
public class OrderPlacedEvent {
    private Long orderId;
    private Long farmId;
    private Long retailerId;
    private String pickupAddress;
    private String deliveryAddress;
    // Các thông tin khác nếu cần (tổng tiền, số lượng...)
}