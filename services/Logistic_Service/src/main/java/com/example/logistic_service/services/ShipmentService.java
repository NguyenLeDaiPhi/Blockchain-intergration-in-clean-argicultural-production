package com.example.logistic_service.services;

import com.example.logistic_service.dto.OrderPlacedEvent;
import com.example.logistic_service.dto.ShipmentStatusEvent;
import com.example.logistic_service.entity.Driver;
import com.example.logistic_service.entity.Shipment;
import com.example.logistic_service.rabbitmq.NotificationProducer;
import com.example.logistic_service.repository.DriverRepository;
import com.example.logistic_service.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired private ShipmentRepository shipmentRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private NotificationProducer notificationProducer;

    // Chức năng 1: Tạo chuyến hàng từ đơn hàng
    public void createShipment(OrderPlacedEvent event) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(event.getOrderId());
        shipment.setFarmId(event.getFarmId());
        shipment.setRetailerId(event.getRetailerId());
        shipment.setPickupAddress(event.getPickupAddress());
        shipment.setDeliveryAddress(event.getDeliveryAddress());
        shipment.setStatus("PENDING"); // Chờ tài xế

        shipmentRepository.save(shipment);
    }

    // Gán tài xế cho chuyến hàng
    public Shipment assignDriver(Long shipmentId, Long driverId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        shipment.setDriverId(driver.getId());
        shipment.setStatus("ASSIGNED"); // Đã có tài xế
        return shipmentRepository.save(shipment);
    }

    // Chức năng 3: Cập nhật trạng thái & Gửi thông báo
    public Shipment updateStatus(Long shipmentId, String status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(status);
        if ("DELIVERED".equals(status)) {
            shipment.setActualDelivery(java.time.LocalDate.now());
        }
        Shipment updatedShipment = shipmentRepository.save(shipment);

        // Bắn sự kiện lên RabbitMQ để Farm/Retailer biết
        ShipmentStatusEvent event = new ShipmentStatusEvent(
                updatedShipment.getId(),
                updatedShipment.getOrderId(),
                updatedShipment.getFarmId(),
                updatedShipment.getRetailerId(),
                updatedShipment.getStatus(),
                LocalDateTime.now()
        );
        notificationProducer.sendStatusUpdate(event);

        return updatedShipment;
    }
    // 4. Chức năng xem chi tiết 1 chuyến hàng (theo ID)
    public Shipment getShipmentDetail(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến hàng với ID: " + shipmentId));
    }

    // 5. Chức năng xem lịch sử các chuyến hàng của Tài xế
    public List<Shipment> getShipmentsByDriver(Long driverId) {
        // Kiểm tra xem tài xế có tồn tại không (nếu cần kỹ hơn)
        if (!driverRepository.existsById(driverId)) {
            throw new RuntimeException("Tài xế không tồn tại!");
        }
        return shipmentRepository.findByDriverId(driverId);
    }
}