package com.bicap.farm_management.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockchainMessage implements Serializable {
    private String resourceId;    // ID của Lô sản xuất hoặc Quy trình
    private String resourceType;  // "BATCH" hoặc "PROCESS"
    private String dataHash;      // Mã Hash dữ liệu
    private String timestamp;
}