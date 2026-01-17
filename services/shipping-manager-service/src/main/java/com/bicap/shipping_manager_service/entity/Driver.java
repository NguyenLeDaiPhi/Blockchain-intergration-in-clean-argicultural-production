package com.bicap.shipping_manager_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId; // ID tài khoản từ Auth Service

    private String fullName;
    private String licenseNumber;
    private String phoneNumber;
    private String status; // AVAILABLE, BUSY
}