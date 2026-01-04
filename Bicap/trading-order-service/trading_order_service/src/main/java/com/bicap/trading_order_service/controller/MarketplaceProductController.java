package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CreateMarketplaceProductRequest;
import com.bicap.trading_order_service.dto.ProductResponse;
import com.bicap.trading_order_service.entity.MarketplaceProduct;
import com.bicap.trading_order_service.security.annotation.CurrentUser;
import com.bicap.trading_order_service.security.jwt.JwtUser;
import com.bicap.trading_order_service.service.IMarketplaceProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace-products")
public class MarketplaceProductController {

    private final IMarketplaceProductService service;

    public MarketplaceProductController(IMarketplaceProductService service) {
        this.service = service;
    }

    // ✅ TEST JWT
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CurrentUser JwtUser user) {
        return ResponseEntity.ok(user);
    }

    // FARM – tạo sản phẩm
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketplaceProduct createProduct(
            @Valid @RequestBody CreateMarketplaceProductRequest request) {
        return service.createProduct(request);
    }

    // RETAILER – danh sách sản phẩm
    @GetMapping
    public List<ProductResponse> getApprovedProducts() {
        return service.getApprovedProducts();
    }

    // RETAILER – chi tiết
    @GetMapping("/{id}")
    public ProductResponse getProductDetail(@PathVariable Long id) {
        return service.getProductDetail(id);
    }
}
