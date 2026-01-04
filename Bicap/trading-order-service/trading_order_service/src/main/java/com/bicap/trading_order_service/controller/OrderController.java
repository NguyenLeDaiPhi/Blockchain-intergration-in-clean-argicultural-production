package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.dto.OrderResponse;
import com.bicap.trading_order_service.security.annotation.CurrentUser;
import com.bicap.trading_order_service.security.jwt.JwtUser;
import com.bicap.trading_order_service.service.IOrderService;
import com.bicap.trading_order_service.service.OrderService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * TEST JWT – lấy info user hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyOrders(@CurrentUser JwtUser user) {
        return ResponseEntity.ok(user);
    }

    /**
     * Retailer tạo đơn hàng
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * Farm xem đơn theo farmId
     */
    @GetMapping("/by-farm/{farmId}")
    public List<OrderResponse> getOrdersByFarm(
            @PathVariable Long farmId
    ) {
        return orderService.getOrdersByFarm(farmId);
    }

    /**
     * Hoàn tất đơn hàng
     */
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    /**
     * Farm xác nhận đơn
     */
    @PutMapping("/{orderId}/confirm")
    public OrderResponse confirmOrder(@PathVariable Long orderId) {
        return orderService.confirmOrder(orderId);
    }

    /**
     * Farm từ chối đơn
     */
    @PutMapping("/{orderId}/reject")
    public OrderResponse rejectOrder(@PathVariable Long orderId) {
        return orderService.rejectOrder(orderId);
    }
}

