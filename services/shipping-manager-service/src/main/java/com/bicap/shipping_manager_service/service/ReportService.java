package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.entity.Shipment;
import com.bicap.shipping_manager_service.entity.ShipmentStatus;
import com.bicap.shipping_manager_service.repository.DriverRepository;
import com.bicap.shipping_manager_service.repository.ShipmentRepository;
import com.bicap.shipping_manager_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public Map<String, Object> getSummaryReport() {
        Map<String, Object> report = new HashMap<>();
        List<Shipment> allShipments = shipmentRepository.findAll();

        report.put("totalShipments", allShipments.size());
        report.put("totalDrivers", driverRepository.count());
        report.put("totalVehicles", vehicleRepository.count());
        report.put("deliveredShipments", allShipments.stream().filter(s -> s.getStatus() == ShipmentStatus.DELIVERED).count());
        report.put("inTransitShipments", allShipments.stream().filter(s -> s.getStatus() == ShipmentStatus.IN_TRANSIT).count());
        
        return report;
    }
}