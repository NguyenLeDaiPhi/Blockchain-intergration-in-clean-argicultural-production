package com.example.admin_service.controller;

import com.example.admin_service.client.TradingProductServiceClient;
import com.example.admin_service.dto.AdminProductResponseDTO;
import com.example.admin_service.dto.BanProductRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Admin Product Monitoring", description = "APIs giám sát sản phẩm dành cho Admin")
public class AdminProductController {

    @Autowired
    private TradingProductServiceClient tradingProductServiceClient;

    /**
     * GET /api/v1/admin/products - Lấy danh sách sản phẩm với bộ lọc
     */
    @GetMapping
    public ResponseEntity<Page<AdminProductResponseDTO>> getProducts(
        @RequestHeader("Authorization") String authorization,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long farmId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminProductResponseDTO> products = tradingProductServiceClient.getProducts(authorization, keyword, status, farmId, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1/admin/products/{id} - Lấy chi tiết sản phẩm
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Xem chi tiết một sản phẩm theo ID")
    public ResponseEntity<AdminProductResponseDTO> getProductById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        AdminProductResponseDTO product = tradingProductServiceClient.getProductById(authorization, id);
        return ResponseEntity.ok(product);
    }

    /**
     * PUT /api/v1/admin/products/{id}/ban - Khóa sản phẩm
     */
    @PutMapping("/{id}/ban")
    @Operation(summary = "Khóa sản phẩm", 
               description = "Admin khóa sản phẩm vi phạm. Cần cung cấp lý do khóa.")
    public ResponseEntity<AdminProductResponseDTO> banProduct(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @Valid @RequestBody BanProductRequestDTO request
    ) {
        AdminProductResponseDTO bannedProduct = tradingProductServiceClient.banProduct(authorization, id, request);
        return ResponseEntity.ok(bannedProduct);
    }

    /**
     * PUT /api/v1/admin/products/{id}/unban - Mở khóa sản phẩm
     */
    @PutMapping("/{id}/unban")
    @Operation(summary = "Mở khóa sản phẩm", 
               description = "Admin mở khóa sản phẩm sau khi Farmer đã sửa lỗi và khiếu nại thành công")
    public ResponseEntity<AdminProductResponseDTO> unbanProduct(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        AdminProductResponseDTO unbannedProduct = tradingProductServiceClient.unbanProduct(authorization, id);
        return ResponseEntity.ok(unbannedProduct);
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countByStatus(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String status
    ) {
        return ResponseEntity.ok(tradingProductServiceClient.countByStatus(authorization, status));
    }

    /**
     * PUT /api/v1/admin/products/{id}/approve - Duyệt sản phẩm PENDING
     */
    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt sản phẩm", description = "Admin duyệt sản phẩm PENDING lên sàn")
    public ResponseEntity<AdminProductResponseDTO> approveProduct(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        AdminProductResponseDTO approvedProduct = tradingProductServiceClient.approveProduct(authorization, id);
        return ResponseEntity.ok(approvedProduct);
    }

    /**
     * PUT /api/v1/admin/products/{id}/reject - Từ chối sản phẩm PENDING
     */
    @PutMapping("/{id}/reject")
    @Operation(summary = "Từ chối sản phẩm", description = "Admin từ chối sản phẩm PENDING với lý do")
    public ResponseEntity<AdminProductResponseDTO> rejectProduct(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @Valid @RequestBody BanProductRequestDTO request
    ) {
        AdminProductResponseDTO rejectedProduct = tradingProductServiceClient.rejectProduct(authorization, id, request);
        return ResponseEntity.ok(rejectedProduct);
    }

    /**
     * GET /api/v1/admin/products/statistics - Thống kê sản phẩm
     */
    @GetMapping("/statistics")
    @Operation(summary = "Thống kê sản phẩm", description = "Lấy số lượng sản phẩm theo từng trạng thái")
    public ResponseEntity<Map<String, Object>> getProductStatistics(
            @RequestHeader("Authorization") String authorization
    ) {
        Map<String, Object> stats = new HashMap<>();
            stats.put("totalActive", tradingProductServiceClient.countByStatus(authorization, "APPROVED"));
            stats.put("totalBanned", tradingProductServiceClient.countByStatus(authorization, "BANNED"));
            stats.put("totalOutOfStock", tradingProductServiceClient.countByStatus(authorization, "OUT_OF_STOCK"));
            stats.put("totalPending", tradingProductServiceClient.countByStatus(authorization, "PENDING"));
        return ResponseEntity.ok(stats);
    }
}
