package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.entity.Driver;
import com.bicap.shipping_manager_service.entity.ShipmentStatus;
import com.bicap.shipping_manager_service.repository.DriverRepository;
import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final ShipmentRepository shipmentRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }
    
    public List<Driver> searchDriversByName(String name) {
        return driverRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Driver updateDriver(Long id, Driver driver) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        // Update fields
        if (driver.getName() != null) {
            existing.setName(driver.getName());
        }
        if (driver.getPhone() != null) {
            existing.setPhone(driver.getPhone());
        }
        if (driver.getLicense() != null) {
            existing.setLicense(driver.getLicense());
        }
        if (driver.getUserId() != null) {
            existing.setUserId(driver.getUserId());
        }
        
        return driverRepository.save(existing);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        // Check if driver is currently assigned to active shipments
        boolean isInUse = shipmentRepository.findByDriverId(id).stream()
                .anyMatch(shipment -> shipment.getStatus() != ShipmentStatus.DELIVERED 
                                   && shipment.getStatus() != ShipmentStatus.CANCELLED);
        
        if (isInUse) {
            throw new RuntimeException("Cannot delete driver that is currently assigned to an active shipment");
        }
        
        driverRepository.delete(driver);
    }
}
