package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.dto.OrderResponse;
import com.bicap.trading_order_service.service.IOrderService;
import com.bicap.trading_order_service.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * ✅ TEST JWT – kiểm tra token + role hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<?> me() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthenticated");
        }

        return ResponseEntity.ok(
                new JwtTestResponse(
                        authentication.getName(),
                        authentication.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * 🛒 Retailer tạo đơn hàng
     * 👉 chỉ cần ROLE_RETAILER
     */
    @PreAuthorize("hasRole('RETAILER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * 🌾 Farm Manager xem đơn theo farmId
     */
    @PreAuthorize("hasRole('FARMMANAGER')")
    @GetMapping("/by-farm/{farmId}")
    public List<OrderResponse> getOrdersByFarm(
            @PathVariable Long farmId
    ) {
        return orderService.getOrdersByFarm(farmId);
    }

    /**
     * 🚚 Shipping Manager hoàn tất đơn hàng
     */
    @PreAuthorize("hasRole('SHIPPINGMANAGER')")
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    /**
     * 🌾 Farm Manager xác nhận đơn
     */
    @PreAuthorize("hasRole('FARMMANAGER')")
    @PutMapping("/{orderId}/confirm")
    public OrderResponse confirmOrder(
            @PathVariable Long orderId
    ) {
        return orderService.confirmOrder(orderId);
    }

    /**
     * 🌾 Farm Manager từ chối đơn
     */
    @PreAuthorize("hasRole('FARMMANAGER')")
    @PutMapping("/{orderId}/reject")
    public OrderResponse rejectOrder(
            @PathVariable Long orderId
    ) {
        return orderService.rejectOrder(orderId);
    }

    /**
     * 🔹 DTO nhỏ để test JWT
     */
    static class JwtTestResponse {
        public String username;
        public List<String> roles;

        public JwtTestResponse(String username, List<String> roles) {
            this.username = username;
            this.roles = roles;
        }
    }
}
