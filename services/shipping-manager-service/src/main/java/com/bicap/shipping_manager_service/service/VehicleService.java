package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.entity.Vehicle;
import com.bicap.shipping_manager_service.entity.ShipmentStatus;
import com.bicap.shipping_manager_service.repository.VehicleRepository;
import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicle.getStatus() == null) vehicle.setStatus("AVAILABLE");
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        
        // Update fields
        if (vehicle.getPlate() != null) {
            existing.setPlate(vehicle.getPlate());
        }
        if (vehicle.getType() != null) {
            existing.setType(vehicle.getType());
        }
        if (vehicle.getStatus() != null) {
            existing.setStatus(vehicle.getStatus());
        }
        
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        
        // Check if vehicle is currently in use (has active shipments)
        boolean isInUse = shipmentRepository.findByVehicleId(id).stream()
                .anyMatch(shipment -> shipment.getStatus() != ShipmentStatus.DELIVERED 
                                   && shipment.getStatus() != ShipmentStatus.CANCELLED);
        
        if (isInUse) {
            throw new RuntimeException("Cannot delete vehicle that is currently assigned to an active shipment");
        }
        
        vehicleRepository.delete(vehicle);
    }
}
