package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.dto.ProductImageDto;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.FileUploadService;
import com.ecommerce.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Map;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Products", description = "Product management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    
    private final ProductService productService;
    private final FileUploadService fileUploadService;
    private final ProductReviewService productReviewService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Create a new product", description = "Creates a new product listing (Seller/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        ProductDto product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ecommerce.dto.ApiResponse.success("Product created successfully", product));
    }
    
    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("Updating product: {}", productId);
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Product updated successfully", product));
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProduct(@PathVariable Long productId) {
        ProductDto product = productService.getProductById(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(product));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductBySlug(@PathVariable String slug) {
        ProductDto product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(product));
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search products", description = "Search products with filters including keyword, category, price range, and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results returned successfully",
            content = @Content(schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> searchProducts(
            @Valid @RequestBody ProductSearchRequest request) {
        List<ProductDto> products = productService.searchProducts(request);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(products));
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductsBySeller(
            @PathVariable Long sellerId) {
        List<ProductDto> products = productService.getProductsBySeller(sellerId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductsByCategory(
            @PathVariable Long categoryId) {
        List<ProductDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(products));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getFeaturedProducts(
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        List<ProductDto> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(products));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getLatestProducts(
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        List<ProductDto> products = productService.getLatestProducts(limit);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(products));
    }
    
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Product deleted successfully"));
    }
    
    @PatchMapping("/{productId}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam String status) {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .status(Product.ProductStatus.valueOf(status.toUpperCase()))
                .build();
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Product status updated successfully", product));
    }
    
    @PatchMapping("/{productId}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> toggleFeatured(
            @PathVariable Long productId,
            @RequestParam Boolean featured) {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .featured(featured)
                .build();
        ProductDto product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Product featured status updated", product));
    }
    
    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Upload product image", description = "Upload an image for a product (Seller/Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image uploaded successfully",
            content = @Content(schema = @Schema(implementation = ProductImageDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file or parameters",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class))),
        @ApiResponse(responseCode = "500", description = "Server error during upload",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "displayOrder", defaultValue = "0") Integer displayOrder,
            @RequestParam(value = "altText", defaultValue = "") String altText) {
        try {
            log.info("Uploading image for product: {}", productId);
            String imageUrl = fileUploadService.uploadProductImage(file);
            
            ProductImageDto imageDto = productService.addProductImage(productId, imageUrl, altText, displayOrder);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(com.ecommerce.dto.ApiResponse.success("Image uploaded successfully", imageDto));
        } catch (IOException e) {
            log.error("Error uploading image for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(com.ecommerce.dto.ApiResponse.error("Failed to upload image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        log.info("Deleting image {} for product: {}", imageId, productId);
        
        ProductImageDto imageDto = productService.getProductImage(imageId);
        if (imageDto != null) {
            fileUploadService.deleteFile(imageDto.getImageUrl());
        }
        
        productService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Image deleted successfully"));
    }
    
    @PutMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> updateProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            @RequestParam(value = "altText", required = false) String altText) {
        log.info("Updating image {} for product: {}", imageId, productId);
        
        ProductImageDto imageDto = productService.updateProductImage(productId, imageId, altText, displayOrder);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Image updated successfully", imageDto));
    }
    
    // Product Review Endpoints
    @GetMapping("/{productId}/reviews")
    @Operation(summary = "Get product reviews", description = "Get all approved reviews for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProductReviewDto.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductReviews(@PathVariable Long productId) {
        List<ProductReviewDto> reviews = productReviewService.getProductReviews(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(reviews));
    }
    
    @PostMapping("/{productId}/reviews")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    @Operation(summary = "Create product review", description = "Create a new review for a product (authenticated users only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review created successfully",
            content = @Content(schema = @Schema(implementation = ProductReviewDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or already reviewed",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> createProductReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {
        request.setProductId(productId); // Ensure the product ID matches the path
        ProductReviewDto review = productReviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ecommerce.dto.ApiResponse.success("Review created successfully", review));
    }
    
    @GetMapping("/{productId}/reviews/stats")
    @Operation(summary = "Get product rating statistics", description = "Get average rating and total review count for a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> getProductRatingStats(@PathVariable Long productId) {
        Map<String, Object> stats = productReviewService.getProductRatingStats(productId);
        return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(stats));
    }
}