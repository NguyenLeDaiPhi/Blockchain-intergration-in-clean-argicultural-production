package com.bicap.farm_production.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bicap.farm_production.entity.FarmingProcess;

@Repository
public interface FarmingProcessRepository extends JpaRepository<FarmingProcess, Long> {
    List<FarmingProcess> findBySeasonId(Long seasonId);
}