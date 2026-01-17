package com.bicap.shipping_manager_service.controller;

import com.bicap.shipping_manager_service.entity.Driver;
import com.bicap.shipping_manager_service.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<List<Driver>> searchDrivers(@RequestParam String name) {
        return ResponseEntity.ok(driverService.searchDriversByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SHIPPING_MANAGER', 'ADMIN')")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.createDriver(driver));
    }
}