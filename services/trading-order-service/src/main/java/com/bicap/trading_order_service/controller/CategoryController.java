package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CategoryResponseDTO;
import com.bicap.trading_order_service.service.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/v1/categories - Lấy danh sách danh mục đang active
     * Public API cho tất cả người dùng (Admin, Farmer, Retailer, v.v.)
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    /**
     * GET /api/v1/categories/{id} - Lấy chi tiết danh mục theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
