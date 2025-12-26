package com.bicap.trading_order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    public OrderResponse(Long orderId,
                            BigDecimal totalAmount,
                            String status,
                            LocalDateTime createdAt) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
