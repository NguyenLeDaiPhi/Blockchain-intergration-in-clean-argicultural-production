package com.bicap.farm_production.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockchainResult {
    private String resourceId;
    private boolean success;
    private String transactionId; // TxID từ VeChain
    private String errorMessage;
}