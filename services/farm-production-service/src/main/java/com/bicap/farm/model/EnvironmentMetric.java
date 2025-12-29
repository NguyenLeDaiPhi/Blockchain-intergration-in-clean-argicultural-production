package com.bicap.farm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "environment_metrics")
public class EnvironmentMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long farmId;
    private Long batchId;
    private Double temperature;
    private Double humidity;
    private LocalDateTime recordedAt;

    // --- CÁC HÀM SETTER (GIÚP HẾT LỖI ĐỎ Ở SERVICE) ---
    public void setFarmId(Long farmId) { this.farmId = farmId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
    public Long getFarmId() { return farmId; }
    public Long getBatchId() { return batchId; }
    public Double getTemperature() { return temperature; }
    public Double getHumidity() { return humidity; }
    public java.time.LocalDateTime getRecordedAt() { return recordedAt; }
}