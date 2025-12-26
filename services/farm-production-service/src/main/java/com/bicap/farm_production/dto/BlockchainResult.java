package com.bicap.farm_production.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainResult implements Serializable {
    private String resourceId;    // ID của Season hoặc Process
    private String resourceType;  // "SEASON" hoặc "PROCESS" (QUAN TRỌNG)
    private String transactionId; // Hash giao dịch trên Blockchain
    private boolean success;      // Thành công hay thất bại
    private String errorMessage;
}