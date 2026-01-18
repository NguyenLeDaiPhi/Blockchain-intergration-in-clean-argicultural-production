package com.bicap.trading_order_service.repository;

import com.bicap.trading_order_service.entity.MarketplaceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceProductRepository extends JpaRepository<MarketplaceProduct, Long>, JpaSpecificationExecutor<MarketplaceProduct> {

    @Query("SELECT p FROM MarketplaceProduct p WHERE p.farmManager.farmId = :farmId")
    List<MarketplaceProduct> findByFarmId(@Param("farmId") Long farmId);

    // Tìm sản phẩm theo status
    List<MarketplaceProduct> findByStatus(String status);

    // Tìm sản phẩm theo tên (LIKE)
    @Query("SELECT p FROM MarketplaceProduct p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MarketplaceProduct> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    // Đếm số sản phẩm theo status
    long countByStatus(String status);

    // Admin: Tìm kiếm với bộ lọc (keyword, status, farmId) với phân trang
    @Query("SELECT p FROM MarketplaceProduct p " +
           "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:farmId IS NULL OR p.farmManager.farmId = :farmId)")
    Page<MarketplaceProduct> findWithFilters(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("farmId") Long farmId,
            Pageable pageable);
}