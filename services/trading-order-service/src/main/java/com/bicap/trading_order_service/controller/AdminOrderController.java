package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.OrderResponse;
import com.bicap.trading_order_service.dto.OrderStatisticsDTO;
import com.bicap.trading_order_service.service.IAdminOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final IAdminOrderService adminOrderService;

    public AdminOrderController(IAdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    /**
     * GET /api/admin/orders - Lấy tất cả đơn hàng
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    /**
     * GET /api/admin/orders/{orderId} - Lấy chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminOrderService.getOrderById(orderId));
    }

    /**
     * GET /api/admin/orders/status/{status} - Lấy đơn hàng theo trạng thái
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(adminOrderService.getOrdersByStatus(status));
    }

    /**
     * GET /api/admin/orders/count - Đếm tổng số đơn hàng
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTotalOrders() {
        return ResponseEntity.ok(adminOrderService.countTotalOrders());
    }

    /**
     * GET /api/admin/orders/count/{status} - Đếm số đơn hàng theo trạng thái
     */
    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countOrdersByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(adminOrderService.countOrdersByStatus(status));
    }

    /**
     * GET /api/admin/orders/statistics - Thống kê đơn hàng
     */
    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsDTO> getOrderStatistics() {
        return ResponseEntity.ok(adminOrderService.getOrderStatistics());
    }
}
