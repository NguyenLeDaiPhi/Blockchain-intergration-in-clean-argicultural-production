package com.bicap.shipping_manager_service.entity;

public enum ShipmentStatus {
    PENDING,    // Mới tạo, chưa gán xe
    ASSIGNED,   // Đã gán tài xế và xe
    IN_TRANSIT, // Đang vận chuyển
    DELIVERED,  // Đã giao hàng thành công
    CANCELLED   // Đã hủy
}