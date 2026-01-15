package com.bicap.shipping_manager_service.entity;

import java.sql.Driver;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "shipments")
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId; // ID đơn hàng từ Order Service (đã thành công) 
    
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String fromLocation; // Địa chỉ Farm
    private String toLocation;   // Địa chỉ Retailer
    
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // CREATED, IN_TRANSIT, DELIVERED, CANCELLED

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Các trường audit log
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

enum ShipmentStatus {
    CREATED, ASSIGNED, IN_TRANSIT, COMPLETED, CANCELLED
}
