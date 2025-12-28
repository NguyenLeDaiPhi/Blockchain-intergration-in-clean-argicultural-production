package com.example.logistic_service.controller;

import com.example.logistic_service.entity.Driver;
import com.example.logistic_service.entity.Shipment;
import com.example.logistic_service.services.DriverService;
import com.example.logistic_service.services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logistics")
public class LogisticController {

    @Autowired private DriverService driverService;
    @Autowired private ShipmentService shipmentService;

    // --- Quản lý tài xế ---
    @PostMapping("/drivers")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.addDriver(driver));
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // --- Quản lý đơn hàng ---
    // API cho Admin/Manager gán tài xế
    @PutMapping("/shipments/{shipmentId}/assign-driver/{driverId}")
    public ResponseEntity<Shipment> assignDriver(@PathVariable Long shipmentId,
                                                 @PathVariable Long driverId) {
        return ResponseEntity.ok(shipmentService.assignDriver(shipmentId, driverId));
    }

    // API cho Tài xế cập nhật trạng thái (PICKED_UP, DELIVERED...)
    @PutMapping("/shipments/{shipmentId}/status")
    public ResponseEntity<Shipment> updateStatus(@PathVariable Long shipmentId,
                                                 @RequestParam String status) {
        return ResponseEntity.ok(shipmentService.updateStatus(shipmentId, status));
    }
    @GetMapping("/shipments/{id}")
    public ResponseEntity<Shipment> getShipmentDetail(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentDetail(id));
    }

    // API 2: Xem danh sách các đơn hàng của một tài xế (Lịch sử chạy)
    @GetMapping("/driver/{driverId}/shipments")
    public ResponseEntity<List<Shipment>> getShipmentsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(shipmentService.getShipmentsByDriver(driverId));
    }
}