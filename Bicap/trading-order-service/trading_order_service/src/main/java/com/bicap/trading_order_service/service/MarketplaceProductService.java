package com.bicap.trading_order_service.service;

import com.bicap.trading_order_service.dto.CreateMarketplaceProductRequest;
import com.bicap.trading_order_service.entity.MarketplaceProduct;
import com.bicap.trading_order_service.repository.MarketplaceProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketplaceProductService implements IMarketplaceProductService {

    private final MarketplaceProductRepository repository;

    public MarketplaceProductService(MarketplaceProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public MarketplaceProduct createProduct(CreateMarketplaceProductRequest request) {

        MarketplaceProduct product = new MarketplaceProduct();
        product.setBatchId(request.getBatchId());
        product.setFarmId(request.getFarmId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        // Farm đăng sản phẩm → mặc định PENDING
        product.setStatus("PENDING");
        return repository.save(product);
    }
}
