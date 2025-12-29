package com.bicap.farm.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "farming_logs")
public class FarmingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long batchId;
    private String activity;
    private String description;
    private LocalDate logDate;

    // --- CÁC HÀM SETTER (GIÚP HẾT LỖI ĐỎ Ở SERVICE) ---
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public void setActivity(String activity) { this.activity = activity; }
    public void setDescription(String description) { this.description = description; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    // --- CÁC HÀM GETTER (NẾU CẦN DÙNG) ---
    public Long getBatchId() { return batchId; }
    public String getActivity() { return activity; }
    public String getDescription() { return description; }
    public java.time.LocalDate getLogDate() { return logDate; }
}