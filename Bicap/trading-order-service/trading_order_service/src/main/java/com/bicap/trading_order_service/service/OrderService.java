package com.bicap.trading_order_service.service;

import com.bicap.trading_order_service.dto.CreateOrderRequest;
import com.bicap.trading_order_service.dto.OrderItemRequest;
import com.bicap.trading_order_service.entity.MarketplaceProduct;
import com.bicap.trading_order_service.entity.Order;
import com.bicap.trading_order_service.entity.OrderItem;
import com.bicap.trading_order_service.repository.MarketplaceProductRepository;
import com.bicap.trading_order_service.repository.OrderItemRepository;
import com.bicap.trading_order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MarketplaceProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MarketplaceProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        Order order = new Order();
        order.setBuyerId(request.getBuyerId());
        order.setStatus("CREATED");
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            MarketplaceProduct product = productRepository
                    .findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setQuantity(itemReq.getQuantity());

            //  GIÁ TẠI THỜI ĐIỂM MUA
            item.setUnitPrice(product.getPrice());

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()))
            );

            items.add(item);
        }

        orderItemRepository.saveAll(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Override
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }
}
