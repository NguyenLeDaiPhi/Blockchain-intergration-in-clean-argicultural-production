package com.bicap.farm_production.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "farming_processes")
@Data
public class FarmingProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private FarmingSeason season;

    private String activityType;    // VD: Watering, Fertilizing, Spraying
    private String description;     // Chi tiết: "Tưới 50 lít nước", "Bón phân NPK"
    
    @Column(name = "performed_date")
    private LocalDateTime performedDate;

    // Blockchain integration cho từng hoạt động
    @Column(name = "blockchain_tx_id")
    private String blockchainTxId;

    @Column(name = "data_hash")
    private String dataHash;
    
    @Enumerated(EnumType.STRING)
    private SeasonStatus syncStatus; // Tận dụng enum SeasonStatus hoặc tạo mới ProcessStatus
}
