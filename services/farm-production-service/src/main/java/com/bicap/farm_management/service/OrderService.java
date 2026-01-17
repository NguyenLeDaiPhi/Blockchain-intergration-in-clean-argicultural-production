package com.bicap.farm_management.service;

import com.bicap.farm_management.entity.Order;
import com.bicap.farm_management.entity.OrderStatus;
import com.bicap.farm_management.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void updateOrderStatusFromShipment(Long orderId, String shipmentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

        // Mapping trạng thái từ Shipping Service sang Farm Service
        switch (shipmentStatus) {
            case "IN_TRANSIT":
                order.setStatus(OrderStatus.SHIPPING);
                break;
            case "DELIVERED":
                order.setStatus(OrderStatus.DELIVERED);
                break;
            case "CANCELLED":
                order.setStatus(OrderStatus.CANCELLED);
                break;
        }
        
        orderRepository.save(order);
    }
}