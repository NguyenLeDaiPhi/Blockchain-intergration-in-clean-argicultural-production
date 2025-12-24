package com.bicap.farm_production.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farming_seasons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmingSeason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;            // Tên mùa vụ (VD: Dưa lưới vụ Xuân 2024)

    private String description;     // Mô tả thêm

    @Column(name = "farm_id", nullable = false)
    private Long farmId;            // ID của trang trại (User ID từ Auth Service)

    private String productType;     // Loại nông sản (VD: Melon, Rice)
    
    private Double area;            // Diện tích canh tác (m2)

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expected_harvest_date")
    private LocalDateTime expectedHarvestDate;

    @Column(name = "actual_harvest_date")
    private LocalDateTime actualHarvestDate;

    // --- QUẢN LÝ TRẠNG THÁI ---
    @Enumerated(EnumType.STRING)
    private SeasonStatus status;

    // --- TÍCH HỢP BLOCKCHAIN ---
    
    @Column(name = "blockchain_tx_id", length = 66) // TxID thường dài 66 ký tự (0x...)
    private String blockchainTxId;

    @Column(name = "data_hash", length = 64)        // SHA-256 hash dài 64 ký tự
    private String dataHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = SeasonStatus.PLANNING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}