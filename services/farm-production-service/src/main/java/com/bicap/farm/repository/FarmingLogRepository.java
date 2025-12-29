package com.bicap.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bicap.farm.model.FarmingLog;

@Repository
public interface FarmingLogRepository extends JpaRepository<FarmingLog, Long> {
    // Lấy nhật ký theo Lô sản xuất
    List<FarmingLog> findByBatchId(Long batchId);
}
