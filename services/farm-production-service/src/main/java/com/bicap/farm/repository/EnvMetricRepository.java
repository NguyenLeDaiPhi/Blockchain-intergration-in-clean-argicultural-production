package com.bicap.farm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bicap.farm.model.EnvironmentMetric;

@Repository
public interface EnvMetricRepository extends JpaRepository<EnvironmentMetric, Long> {
    // Lấy danh sách môi trường theo Lô sản xuất, sắp xếp mới nhất lên đầu
    List<EnvironmentMetric> findByBatchIdOrderByRecordedAtDesc(Long batchId);
}
