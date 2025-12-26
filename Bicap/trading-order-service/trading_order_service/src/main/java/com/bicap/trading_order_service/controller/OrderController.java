package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.entity.Order;
import com.bicap.trading_order_service.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService service;

    public OrderController(IOrderService service) {
        this.service = service;
    }

    /**
     * Retailer tạo đơn hàng
     */
    @PostMapping
    public Order createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return service.createOrder(request);
    }

    /**
     * Hoàn tất đơn hàng (sau khi giao xong)
     */
    @PutMapping("/{orderId}/complete")
    public Order completeOrder(@PathVariable Long orderId) {
        return service.completeOrder(orderId);
    }
}
