package com.bicap.trading_order_service.controller;

import com.bicap.trading_order_service.dto.CategoryRequestDTO;
import com.bicap.trading_order_service.dto.CategoryResponseDTO;
import com.bicap.trading_order_service.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final ICategoryService categoryService;

    public AdminCategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * POST /api/admin/categories - Tạo danh mục mới
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * PUT /api/admin/categories/{id} - Cập nhật danh mục
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * GET /api/admin/categories - Lấy tất cả danh mục (bao gồm cả inactive)
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * GET /api/admin/categories/{id} - Lấy chi tiết danh mục
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * DELETE /api/admin/categories/{id} - Xóa mềm danh mục
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Đã ẩn danh mục thành công");
    }

    /**
     * DELETE /api/admin/categories/{id}/permanent - Xóa vĩnh viễn danh mục
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<String> hardDeleteCategory(@PathVariable Long id) {
        categoryService.hardDeleteCategory(id);
        return ResponseEntity.ok("Đã xóa vĩnh viễn danh mục");
    }
}
