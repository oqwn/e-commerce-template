package com.ecommerce.service;

import com.ecommerce.dto.CreateProductRequest;
import com.ecommerce.dto.UpdateProductRequest;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductSearchRequest;
import com.ecommerce.dto.ProductImageDto;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.mapper.*;
import com.ecommerce.model.*;
import com.ecommerce.util.SecurityUtils;
import com.ecommerce.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductTagMapper productTagMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductVariantMapper productVariantMapper;
    private final InventoryService inventoryService;
    
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Long sellerId = SecurityUtils.getCurrentUserId();
        
        // Validate category exists
        Category category = categoryMapper.findById(request.getCategoryId());
        if (category == null || !category.getIsActive()) {
            throw new ResourceNotFoundException("Category not found or inactive");
        }
        
        // Create product
        Product product = Product.builder()
                .sellerId(sellerId)
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .slug(generateUniqueSlug(request.getName()))
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .cost(request.getCost())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .trackQuantity(request.getTrackQuantity() != null ? request.getTrackQuantity() : true)
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit() != null ? request.getWeightUnit() : "kg")
                .status(Product.ProductStatus.DRAFT)
                .featured(false)
                .build();
        
        productMapper.insert(product);
        
        // Add images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            saveProductImages(product.getId(), request.getImages());
        }
        
        // Add tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            saveProductTags(product.getId(), request.getTags());
        }
        
        // Add attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            saveProductAttributes(product.getId(), request.getAttributes());
        }
        
        // Create initial inventory transaction
        if (product.getQuantity() > 0) {
            inventoryService.addInventoryTransaction(
                product.getId(), 
                null, 
                "STOCK_IN", 
                product.getQuantity(), 
                "INITIAL", 
                null, 
                "Initial inventory"
            );
        }
        
        log.info("Product created successfully: {}", product.getId());
        return getProductById(product.getId());
    }
    
    @Transactional
    public ProductDto updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        // Check ownership
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!product.getSellerId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this product");
        }
        
        // Update basic fields
        if (request.getName() != null) {
            product.setName(request.getName());
            if (!product.getSlug().equals(SlugUtils.generateSlug(request.getName()))) {
                product.setSlug(generateUniqueSlug(request.getName()));
            }
        }
        if (request.getCategoryId() != null) {
            Category category = categoryMapper.findById(request.getCategoryId());
            if (category == null || !category.getIsActive()) {
                throw new ResourceNotFoundException("Category not found or inactive");
            }
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCompareAtPrice() != null) {
            product.setCompareAtPrice(request.getCompareAtPrice());
        }
        if (request.getCost() != null) {
            product.setCost(request.getCost());
        }
        if (request.getTrackQuantity() != null) {
            product.setTrackQuantity(request.getTrackQuantity());
        }
        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }
        if (request.getWeightUnit() != null) {
            product.setWeightUnit(request.getWeightUnit());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
            if (request.getStatus() == Product.ProductStatus.ACTIVE && product.getPublishedAt() == null) {
                product.setPublishedAt(LocalDateTime.now());
            }
        }
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        
        // Handle quantity update
        if (request.getQuantity() != null && !request.getQuantity().equals(product.getQuantity())) {
            int quantityDiff = request.getQuantity() - product.getQuantity();
            product.setQuantity(request.getQuantity());
            
            // Record inventory transaction
            String transactionType = quantityDiff > 0 ? "STOCK_IN" : "STOCK_OUT";
            inventoryService.addInventoryTransaction(
                product.getId(), 
                null, 
                transactionType, 
                Math.abs(quantityDiff), 
                "MANUAL", 
                null, 
                "Manual inventory adjustment"
            );
        }
        
        productMapper.update(product);
        
        // Update images
        if (request.getImages() != null) {
            productImageMapper.deleteByProductId(productId);
            saveProductImages(productId, request.getImages());
        }
        
        // Update tags
        if (request.getTags() != null) {
            productTagMapper.deleteByProductId(productId);
            saveProductTags(productId, request.getTags());
        }
        
        // Update attributes
        if (request.getAttributes() != null) {
            productAttributeMapper.deleteByProductId(productId);
            saveProductAttributes(productId, request.getAttributes());
        }
        
        log.info("Product updated successfully: {}", productId);
        return getProductById(productId);
    }
    
    public ProductDto getProductById(Long productId) {
        Product product = productMapper.findByIdWithStats(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        // Load related data
        product.setImages(productImageMapper.findByProductId(productId));
        product.setTags(productTagMapper.findTagsByProductId(productId));
        product.setAttributes(productAttributeMapper.findByProductId(productId));
        product.setVariants(productVariantMapper.findByProductId(productId));
        product.setSeller(new User()); // Load from UserMapper if needed
        product.setCategory(categoryMapper.findById(product.getCategoryId()));
        
        return ProductDto.fromProduct(product);
    }
    
    public ProductDto getProductBySlug(String slug) {
        Product product = productMapper.findBySlug(slug);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        return getProductById(product.getId());
    }
    
    public List<ProductDto> searchProducts(ProductSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", request.getKeyword());
        params.put("categoryId", request.getCategoryId());
        params.put("sellerId", request.getSellerId());
        params.put("minPrice", request.getMinPrice());
        params.put("maxPrice", request.getMaxPrice());
        params.put("status", "ACTIVE"); // Only show active products in search
        params.put("featured", request.getFeatured());
        params.put("tags", request.getTags());
        params.put("sortBy", request.getSortBy());
        params.put("limit", request.getLimit() != null ? request.getLimit() : 20);
        params.put("offset", request.getOffset() != null ? request.getOffset() : 0);
        
        List<Product> products = productMapper.search(params);
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getProductsBySeller(Long sellerId) {
        List<Product> products = productMapper.findBySellerId(sellerId);
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        List<Product> products = productMapper.findByCategoryId(categoryId);
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getFeaturedProducts(Integer limit) {
        List<Product> products = productMapper.findFeaturedProducts(limit != null ? limit : 10);
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }
    
    public List<ProductDto> getLatestProducts(Integer limit) {
        List<Product> products = productMapper.findLatestProducts(limit != null ? limit : 10);
        return products.stream()
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        // Check ownership
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!product.getSellerId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to delete this product");
        }
        
        productMapper.delete(productId);
        log.info("Product deleted successfully: {}", productId);
    }
    
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        
        while (productMapper.findBySlug(slug) != null) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    private void saveProductImages(Long productId, List<CreateProductRequest.ProductImageRequest> images) {
        for (int i = 0; i < images.size(); i++) {
            CreateProductRequest.ProductImageRequest imageRequest = images.get(i);
            ProductImage image = ProductImage.builder()
                    .productId(productId)
                    .imageUrl(imageRequest.getImageUrl())
                    .altText(imageRequest.getAltText())
                    .displayOrder(i)
                    .isPrimary(i == 0)
                    .build();
            productImageMapper.insert(image);
        }
    }
    
    private void saveProductTags(Long productId, List<String> tags) {
        for (String tag : tags) {
            productTagMapper.insert(productId, tag.toLowerCase().trim());
        }
    }
    
    private void saveProductAttributes(Long productId, Map<String, String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            ProductAttribute attribute = ProductAttribute.builder()
                    .productId(productId)
                    .attributeName(entry.getKey())
                    .attributeValue(entry.getValue())
                    .build();
            productAttributeMapper.insert(attribute);
        }
    }
    
    public ProductImageDto addProductImage(Long productId, String imageUrl, String altText, Integer displayOrder) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        ProductImage image = ProductImage.builder()
                .productId(productId)
                .imageUrl(imageUrl)
                .altText(altText)
                .displayOrder(displayOrder != null ? displayOrder : 0)
                .isPrimary(false)
                .build();
        
        productImageMapper.insert(image);
        log.info("Image added to product {}: {}", productId, imageUrl);
        
        return ProductImageDto.fromProductImage(image);
    }
    
    public ProductImageDto getProductImage(Long imageId) {
        ProductImage image = productImageMapper.findById(imageId);
        return image != null ? ProductImageDto.fromProductImage(image) : null;
    }
    
    public ProductImageDto updateProductImage(Long productId, Long imageId, String altText, Integer displayOrder) {
        ProductImage image = productImageMapper.findById(imageId);
        if (image == null || !image.getProductId().equals(productId)) {
            throw new ResourceNotFoundException("Product image not found");
        }
        
        if (altText != null) {
            image.setAltText(altText);
        }
        if (displayOrder != null) {
            image.setDisplayOrder(displayOrder);
        }
        
        productImageMapper.update(image);
        log.info("Image {} updated for product {}", imageId, productId);
        
        return ProductImageDto.fromProductImage(image);
    }
    
    public void deleteProductImage(Long productId, Long imageId) {
        ProductImage image = productImageMapper.findById(imageId);
        if (image == null || !image.getProductId().equals(productId)) {
            throw new ResourceNotFoundException("Product image not found");
        }
        
        productImageMapper.delete(imageId);
        log.info("Image {} deleted from product {}", imageId, productId);
    }
}