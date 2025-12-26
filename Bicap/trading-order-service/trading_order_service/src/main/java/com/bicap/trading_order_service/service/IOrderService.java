package com.bicap.trading_order_service.service;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.dto.OrderResponse;

public interface IOrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse completeOrder(Long orderId);
}