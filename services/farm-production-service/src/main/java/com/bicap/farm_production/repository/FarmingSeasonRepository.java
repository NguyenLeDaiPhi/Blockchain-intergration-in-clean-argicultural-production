package com.bicap.farm_production.repository;


import com.bicap.farm_production.entity.FarmingSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmingSeasonRepository extends JpaRepository<FarmingSeason, Long> {
}
