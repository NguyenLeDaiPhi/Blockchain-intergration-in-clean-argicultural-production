package com.bicap.farm_production.entity;

public enum SeasonStatus {
    PLANNING,           // Đang lên kế hoạch
    IN_PROGRESS,        // Đang canh tác
    HARVESTED,          // Đã thu hoạch
    COMPLETED,          // Đã xuất bán/đóng mùa vụ
    
    // Trạng thái liên quan Blockchain (có thể tách riêng nếu muốn)
    PENDING_BLOCKCHAIN, // Đang chờ Blockchain Adapter xử lý
    SYNCED_BLOCKCHAIN,  // Đã ghi lên Blockchain thành công
    FAILED_BLOCKCHAIN, FAILED
}
