package com.example.admin_service.dto;

import lombok.Data;

@Data
public class AdminProductResponseDTO {
    private Long id;
    private String name;
    private String categoryName;
    private String description;
    private Integer quantity;
    private String unit;
    private Double price;
    private String imageUrl;
    private String batchId;
    private String status;
    private String banReason;

    private Long farmId;
    private String farmName;
    private String ownerName;
    private String farmManagerEmail;
}