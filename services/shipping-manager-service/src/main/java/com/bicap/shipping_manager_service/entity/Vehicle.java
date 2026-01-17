package com.bicap.shipping_manager_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate; // Biển số xe
    private String model;
    private Double capacity; // Tải trọng (tấn)
    private String status; // AVAILABLE, MAINTENANCE, IN_USE
}