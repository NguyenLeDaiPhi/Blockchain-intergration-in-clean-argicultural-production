package com.bicap.farm_management.entity;

public enum OrderStatus {
    PENDING,    // Chờ xác nhận
    CONFIRMED,  // Đã xác nhận, chờ giao
    SHIPPING,   // Đang giao hàng (tương ứng IN_TRANSIT)
    DELIVERED,  // Đã giao thành công
    CANCELLED   // Đã hủy
}