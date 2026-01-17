package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.security.UserDetailsImpl;
import com.bicap.shipping_manager_service.entity.Driver;
import com.bicap.shipping_manager_service.entity.Shipment;
import com.bicap.shipping_manager_service.entity.ShipmentStatus;
import com.bicap.shipping_manager_service.entity.Vehicle;
import com.bicap.shipping_manager_service.repository.DriverRepository;
import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import com.bicap.shipping_manager_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipmentProducer shipmentProducer;

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment createShipment(Long orderId, String fromLocation, String toLocation) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setFromLocation(fromLocation);
        shipment.setToLocation(toLocation);
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment assignDriverAndVehicle(Long shipmentId, Long driverId, Long vehicleId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vận đơn ID: " + shipmentId));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế ID: " + driverId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe ID: " + vehicleId));

        shipment.setDriver(driver);
        shipment.setVehicle(vehicle);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        shipment.setUpdatedAt(LocalDateTime.now());

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment updateStatus(Long shipmentId, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vận đơn ID: " + shipmentId));

        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Gửi thông báo cập nhật sang Farm Service
        shipmentProducer.sendShipmentStatusUpdate(shipment.getOrderId(), status.name());

        return savedShipment;
    }

    public List<Shipment> getMyShipments() {
        // 1. Lấy thông tin xác thực hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập!");
        }

        // Lấy UserDetails từ Security Context
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        // Tìm Driver dựa trên userId
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa được đăng ký là tài xế trong hệ thống!"));

        return shipmentRepository.findByDriverId(driver.getId());
    }
}