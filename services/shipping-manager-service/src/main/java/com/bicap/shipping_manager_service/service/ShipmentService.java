package com.bicap.shipping_manager_service.service;

import com.bicap.shipping_manager_service.client.BlockchainServiceClient;
import com.bicap.shipping_manager_service.client.FarmServiceClient;
import com.bicap.shipping_manager_service.config.RabbitMQConfig; // NEW: Import Config
import com.bicap.shipping_manager_service.dto.OrderResponse;
import com.bicap.shipping_manager_service.dto.ShipmentEvent; // NEW: Import DTO
import com.bicap.shipping_manager_service.dto.WriteBlockchainRequest;
import com.bicap.shipping_manager_service.entity.*;
import com.bicap.shipping_manager_service.repository.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // NEW: Import RabbitTemplate
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentService.class);
    
    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final FarmServiceClient farmClient;
    private final BlockchainServiceClient blockchainClient;
    private final RabbitTemplate rabbitTemplate; // NEW: Inject RabbitTemplate
    private final Gson gson = new Gson();

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // 1. Tạo vận đơn (Giữ nguyên)
    public Shipment createShipment(Long orderId, String from, String to) {
        try {
            OrderResponse order = farmClient.getOrderDetails(orderId);
            if (order == null) throw new RuntimeException("Order not found");
        } catch (Exception e) {
            logger.error("Error calling Farm Service: {}", e.getMessage());
        }

        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setFromLocation(from);
        shipment.setToLocation(to);
        shipment.setStatus(ShipmentStatus.CREATED);
        
        return shipmentRepository.save(shipment);
    }

    // 2. Điều phối xe và tài xế (Giữ nguyên)
    public Shipment assignDriverAndVehicle(Long shipmentId, Long driverId, Long vehicleId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        shipment.setDriver(driver);
        shipment.setVehicle(vehicle);
        shipment.setStatus(ShipmentStatus.ASSIGNED);
        
        return shipmentRepository.save(shipment);
    }

    // 3. Cập nhật trạng thái & Blockchain & RabbitMQ (Đã cập nhật)
    public Shipment updateStatus(Long shipmentId, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(status);
        // Cập nhật thời gian update
        shipment.setUpdatedAt(LocalDateTime.now());
        
        Shipment saved = shipmentRepository.save(shipment);

        // --- NEW: Gửi sự kiện RabbitMQ khi giao hàng thành công ---
        if (status == ShipmentStatus.DELIVERED) {
            try {
                ShipmentEvent event = new ShipmentEvent(
                    saved.getOrderId(), 
                    "DELIVERED", 
                    "Shipment completed for ID: " + saved.getId()
                );
                
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE, 
                    RabbitMQConfig.ROUTING_KEY, 
                    event
                );
                logger.info("Đã gửi sự kiện DELIVERED cho Order ID: {}", saved.getOrderId());
            } catch (Exception e) {
                logger.error("Lỗi gửi RabbitMQ: {}", e.getMessage());
            }
        }
        // -----------------------------------------------------------

        // Ghi log Blockchain
        try {
            WriteBlockchainRequest req = new WriteBlockchainRequest(
                shipmentId, 
                "SHIPMENT_UPDATE: " + status + " | " + gson.toJson(saved)
            );
            blockchainClient.writeToBlockchain(req);
        } catch (Exception e) {
            logger.error("Blockchain write failed: {}", e.getMessage());
        }

        return saved;
    }
}