package com.bicap.trading_order_service.event;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderConfirmedEvent implements Serializable {

    private Long orderId;
    private String buyerEmail;
    private String farmManagerEmail;
    private Long farmId;
    private BigDecimal totalAmount;
    private String shippingAddress;

    public OrderConfirmedEvent() {
    }

    public OrderConfirmedEvent(Long orderId, String buyerEmail, String farmManagerEmail, Long farmId, BigDecimal totalAmount, String shippingAddress) {
        this.orderId = orderId;
        this.buyerEmail = buyerEmail;
        this.farmManagerEmail = farmManagerEmail;
        this.farmId = farmId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
    }
}
