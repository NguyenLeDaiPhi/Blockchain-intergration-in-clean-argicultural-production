package com.bicap.shipping_manager_service.controller;

import com.bicap.shipping_manager_service.entity.Driver;
import com.bicap.shipping_manager_service.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers") // Endpoint gốc cho tài xế
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    // API: Tìm kiếm tài xế
    // URL: GET /api/drivers/search?name=Tuan
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Driver>> searchDrivers(@RequestParam String name) {
        return ResponseEntity.ok(driverService.searchDriversByName(name));
    }

    // API: Lấy danh sách tất cả tài xế
    @GetMapping
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }
    
    // API: Tạo tài xế mới
    @PostMapping
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.createDriver(driver));
    }
}