package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.entity.Driver;
import com.bicap.shipping_manager_service.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    // Lấy tất cả tài xế
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Tìm tài xế theo tên
    public List<Driver> searchDriversByName(String name) {
        return driverRepository.findByFullNameContainingIgnoreCase(name);
    }
    
    // Tạo mới tài xế (nếu cần dùng cho chức năng quản lý)
    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}