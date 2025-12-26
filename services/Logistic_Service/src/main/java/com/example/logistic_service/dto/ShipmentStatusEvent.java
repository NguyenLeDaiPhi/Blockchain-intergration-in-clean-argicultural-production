package com.example.logistic_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentStatusEvent {
    private Long shipmentId;
    private Long orderId;
    private Long farmId;
    private Long retailerId;
    private String status; // DELIVERED, PICKED_UP...
    private LocalDateTime timestamp;
}