package com.bicap.shipping_manager_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TRƯỜNG NÀY: Liên kết với username từ Auth Service
    @Column(unique = true)
    private String username;

    private String fullName;
    private String licenseNumber;
    private String phoneNumber;
    private String status;
}