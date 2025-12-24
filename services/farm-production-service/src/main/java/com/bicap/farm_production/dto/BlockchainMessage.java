package com.bicap.farm_production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockchainMessage {
    private String resourceId;   // ID mùa vụ (DB ID)
    private String resourceType; // "SEASON"
    private String dataHash;     // Dữ liệu cần ghi
    private String action;       // "CREATE", "UPDATE"
}
