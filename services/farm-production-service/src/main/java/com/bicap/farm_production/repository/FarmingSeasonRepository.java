package com.bicap.farm_production.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bicap.farm_production.entity.FarmingSeason;

@Repository
public interface FarmingSeasonRepository extends JpaRepository<FarmingSeason, Long> {
    // Spring Data JPA sẽ tự động cung cấp các hàm như save(), findAll(), findById()...
}