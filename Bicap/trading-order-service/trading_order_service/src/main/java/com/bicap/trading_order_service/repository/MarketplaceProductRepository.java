package com.bicap.trading_order_service.repository;

import com.bicap.trading_order_service.entity.MarketplaceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketplaceProductRepository
        extends JpaRepository<MarketplaceProduct, Long> {
}
