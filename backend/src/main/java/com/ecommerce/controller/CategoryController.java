package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        CategoryDto category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }
    
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("Updating category: {}", categoryId);
        CategoryDto category = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }
    
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> getCategory(@PathVariable Long categoryId) {
        CategoryDto category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse> getCategoryBySlug(@PathVariable String slug) {
        CategoryDto category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        List<CategoryDto> categories = categoryService.getAllCategories(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/root")
    public ResponseEntity<ApiResponse> getRootCategories() {
        List<CategoryDto> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse> getChildCategories(@PathVariable Long parentId) {
        List<CategoryDto> categories = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {
        log.info("Deleting category: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}