package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.ProductResponseDTO;
import com.bicap.trading_order_service.dto.BanProductRequestDTO;
import com.bicap.trading_order_service.dto.ProductStatisticsDTO;
import com.bicap.trading_order_service.entity.MarketplaceProduct;
import com.bicap.trading_order_service.entity.FarmManager;
import com.bicap.trading_order_service.repository.MarketplaceProductRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class InternalAdminProductController {

    private final MarketplaceProductRepository repository;

    @Autowired
    public InternalAdminProductController(MarketplaceProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/admin/products - Filtered list for Admin
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long farmId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MarketplaceProduct> productsPage = repository.findWithFilters(keyword, status, farmId, pageable);
        
        // Map Entity -> DTO
        Page<ProductResponseDTO> dtoPage = productsPage.map(this::mapToProductResponseDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        MarketplaceProduct product = repository.findByIdWithFarmManager(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(mapToProductResponseDTO(product));
    }

    @PutMapping("/{id}/ban")
    public ResponseEntity<ProductResponseDTO> banProduct(
            @PathVariable Long id,
            @Valid @RequestBody BanProductRequestDTO request
    ) {
        MarketplaceProduct product = repository.findByIdWithFarmManager(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus("BANNED");
        product.setBanReason(request.getReason());
        repository.save(product);
        
        return ResponseEntity.ok(mapToProductResponseDTO(product));
    }

    @PutMapping("/{id}/unban")
    public ResponseEntity<ProductResponseDTO> unbanProduct(@PathVariable Long id) {
        MarketplaceProduct product = repository.findByIdWithFarmManager(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus("APPROVED"); // Or "ACTIVE" based on your business logic
        product.setBanReason(null);
        repository.save(product);
        
        return ResponseEntity.ok(mapToProductResponseDTO(product));
    }

    /**
     * PUT /api/admin/products/{id}/approve - Duyệt sản phẩm PENDING thành APPROVED
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ProductResponseDTO> approveProduct(@PathVariable Long id) {
        MarketplaceProduct product = repository.findByIdWithFarmManager(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus("APPROVED");
        repository.save(product);
        
        return ResponseEntity.ok(mapToProductResponseDTO(product));
    }

    /**
     * PUT /api/admin/products/{id}/reject - Từ chối sản phẩm PENDING
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ProductResponseDTO> rejectProduct(
            @PathVariable Long id,
            @Valid @RequestBody BanProductRequestDTO request
    ) {
        MarketplaceProduct product = repository.findByIdWithFarmManager(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus("REJECTED");
        product.setBanReason(request.getReason());
        repository.save(product);
        
        return ResponseEntity.ok(mapToProductResponseDTO(product));
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable String status) {
        return ResponseEntity.ok(repository.countByStatus(status));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ProductStatisticsDTO> getProductStatistics() {
        ProductStatisticsDTO stats = ProductStatisticsDTO.builder()
                .totalActive(repository.countByStatus("APPROVED"))
                .totalBanned(repository.countByStatus("BANNED"))
                .totalOutOfStock(repository.countByStatus("OUT_OF_STOCK"))
                .totalPending(repository.countByStatus("PENDING"))
                .build();
        return ResponseEntity.ok(stats);
    }

    // === MAPPING HELPER ===
    private ProductResponseDTO mapToProductResponseDTO(MarketplaceProduct product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        
        // Basic fields
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        dto.setUnit(product.getUnit());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setBatchId(product.getBatchId());
        dto.setStatus(product.getStatus());
        dto.setBanReason(product.getBanReason());
        dto.setCategoryName(product.getCategory()); // Assuming category string is stored here

        // Farm & Owner Mapping
        FarmManager fm = product.getFarmManager();
        if (fm != null) {
            dto.setFarmId(fm.getFarmId());
            dto.setOwnerName(fm.getUsername()); // Map username to Owner Name
            dto.setFarmManagerEmail(fm.getEmail());
            
            // Logic for Farm Name: 
            // If FarmManager entity has a farmName field, use it. 
            // Otherwise, fallback to "Farm #" + ID or Username.
            // Assuming for now we use username as fallback if explicit farmName is missing
            dto.setFarmName(fm.getUsername() + "'s Farm"); 
        } else {
            dto.setFarmName("Unknown Farm");
            dto.setOwnerName("Unknown Owner");
        }

        return dto;
    }
}