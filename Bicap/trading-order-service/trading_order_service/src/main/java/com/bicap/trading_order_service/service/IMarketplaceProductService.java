package com.bicap.trading_order_service.service;

import com.bicap.trading_order_service.dto.CreateMarketplaceProductRequest;
import com.bicap.trading_order_service.entity.MarketplaceProduct;

public interface IMarketplaceProductService {

    // Farm đăng sản phẩm lên sàn giao dịch
    
    MarketplaceProduct createProduct(CreateMarketplaceProductRequest request);
}
