package com.bicap.shipping_manager_service.repository;

import com.bicap.shipping_manager_service.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Tìm theo username (đã có)
    Optional<Driver> findByUsername(String username);
    List<Driver> findByFullNameContainingIgnoreCase(String fullName);
}