package com.bicap.trading_order_service.service;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.entity.Order;

public interface IOrderService {

    Order createOrder(CreateOrderRequest request);

    Order completeOrder(Long orderId);
}
