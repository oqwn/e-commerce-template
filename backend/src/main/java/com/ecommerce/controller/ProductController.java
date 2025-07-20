package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.dto.ProductImageDto;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    
    private final ProductService productService;
    private final FileUploadService fileUploadService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        ProductDto product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }
    
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("Updating product: {}", productId);
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long productId) {
        ProductDto product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable String slug) {
        ProductDto product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse> searchProducts(
            @Valid @RequestBody ProductSearchRequest request) {
        List<ProductDto> products = productService.searchProducts(request);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse> getProductsBySeller(
            @PathVariable Long sellerId) {
        List<ProductDto> products = productService.getProductsBySeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getProductsByCategory(
            @PathVariable Long categoryId) {
        List<ProductDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse> getFeaturedProducts(
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        List<ProductDto> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestProducts(
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        List<ProductDto> products = productService.getLatestProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
    
    @PatchMapping("/{productId}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam String status) {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .status(Product.ProductStatus.valueOf(status.toUpperCase()))
                .build();
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product status updated successfully", product));
    }
    
    @PatchMapping("/{productId}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> toggleFeatured(
            @PathVariable Long productId,
            @RequestParam Boolean featured) {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .featured(featured)
                .build();
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product featured status updated", product));
    }
    
    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "displayOrder", defaultValue = "0") Integer displayOrder,
            @RequestParam(value = "altText", defaultValue = "") String altText) {
        try {
            log.info("Uploading image for product: {}", productId);
            String imageUrl = fileUploadService.uploadProductImage(file);
            
            ProductImageDto imageDto = productService.addProductImage(productId, imageUrl, altText, displayOrder);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Image uploaded successfully", imageDto));
        } catch (IOException e) {
            log.error("Error uploading image for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        log.info("Deleting image {} for product: {}", imageId, productId);
        
        ProductImageDto imageDto = productService.getProductImage(imageId);
        if (imageDto != null) {
            fileUploadService.deleteFile(imageDto.getImageUrl());
        }
        
        productService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }
    
    @PutMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            @RequestParam(value = "altText", required = false) String altText) {
        log.info("Updating image {} for product: {}", imageId, productId);
        
        ProductImageDto imageDto = productService.updateProductImage(productId, imageId, altText, displayOrder);
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully", imageDto));
    }
}