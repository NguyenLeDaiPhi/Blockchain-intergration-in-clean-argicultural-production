package com.bicap.trading_order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String categoryName; // Matches EJS: p.categoryName
    private String description;
    private Integer quantity;
    private String unit;
    private Double price;
    private String imageUrl;
    private String batchId;
    private String status;
    private String banReason;

    // Farm details
    private Long farmId;
    private String farmName;   // Matches EJS: p.farmName
    private String ownerName;  // Matches EJS: p.ownerName
    private String farmManagerEmail;
}