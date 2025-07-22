package com.ecommerce.service.impl;

import com.ecommerce.dto.PaginationResponse;
import com.ecommerce.dto.request.CreateFlashSaleRequest;
import com.ecommerce.dto.request.UpdateFlashSaleRequest;
import com.ecommerce.dto.response.FlashSaleResponse;
import com.ecommerce.dto.response.FlashSaleProductResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.FlashSaleMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.model.FlashSale;
import com.ecommerce.model.FlashSaleProduct;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    
    private final FlashSaleMapper flashSaleMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public FlashSaleResponse createFlashSale(Long createdBy, CreateFlashSaleRequest request) {
        // Validate creator exists
        User creator = userMapper.findById(createdBy)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Validate time range
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        // Validate products exist and prices
        for (CreateFlashSaleRequest.FlashSaleProductRequest productReq : request.getProducts()) {
            Product product = productMapper.findById(productReq.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found: " + productReq.getProductId());
            }
            
            if (productReq.getSalePrice().compareTo(productReq.getOriginalPrice()) > 0) {
                throw new IllegalArgumentException("Sale price cannot be higher than original price for product: " + productReq.getProductId());
            }
        }
        
        // Create flash sale
        FlashSale flashSale = FlashSale.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .discountPercentage(request.getDiscountPercentage())
                .maxQuantity(request.getMaxQuantity())
                .usedQuantity(0)
                .isActive(true)
                .createdBy(createdBy)
                .build();
                
        flashSaleMapper.insertFlashSale(flashSale);
        
        // Add products to flash sale
        for (CreateFlashSaleRequest.FlashSaleProductRequest productReq : request.getProducts()) {
            FlashSaleProduct flashSaleProduct = FlashSaleProduct.builder()
                    .flashSaleId(flashSale.getId())
                    .productId(productReq.getProductId())
                    .originalPrice(productReq.getOriginalPrice())
                    .salePrice(productReq.getSalePrice())
                    .maxQuantityPerProduct(productReq.getMaxQuantityPerProduct())
                    .usedQuantityPerProduct(0)
                    .build();
                    
            flashSaleMapper.insertFlashSaleProduct(flashSaleProduct);
        }
        
        log.info("Created flash sale {} by user {}", flashSale.getId(), createdBy);
        return getFlashSaleById(flashSale.getId());
    }
    
    @Override
    @Transactional
    public FlashSaleResponse updateFlashSale(Long id, UpdateFlashSaleRequest request) {
        FlashSale existingFlashSale = flashSaleMapper.findById(id);
        if (existingFlashSale == null) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        
        // Don't allow updates to active flash sales
        if (existingFlashSale.isCurrentlyActive()) {
            throw new IllegalArgumentException("Cannot update an active flash sale");
        }
        
        // Build updated flash sale
        FlashSale.FlashSaleBuilder builder = FlashSale.builder()
                .id(id)
                .createdBy(existingFlashSale.getCreatedBy());
                
        if (request.getName() != null) {
            builder.name(request.getName());
        } else {
            builder.name(existingFlashSale.getName());
        }
        
        if (request.getDescription() != null) {
            builder.description(request.getDescription());
        } else {
            builder.description(existingFlashSale.getDescription());
        }
        
        if (request.getStartTime() != null) {
            builder.startTime(request.getStartTime());
        } else {
            builder.startTime(existingFlashSale.getStartTime());
        }
        
        if (request.getEndTime() != null) {
            builder.endTime(request.getEndTime());
        } else {
            builder.endTime(existingFlashSale.getEndTime());
        }
        
        if (request.getDiscountPercentage() != null) {
            builder.discountPercentage(request.getDiscountPercentage());
        } else {
            builder.discountPercentage(existingFlashSale.getDiscountPercentage());
        }
        
        if (request.getMaxQuantity() != null) {
            builder.maxQuantity(request.getMaxQuantity());
        } else {
            builder.maxQuantity(existingFlashSale.getMaxQuantity());
        }
        
        if (request.getIsActive() != null) {
            builder.isActive(request.getIsActive());
        } else {
            builder.isActive(existingFlashSale.getIsActive());
        }
        
        builder.usedQuantity(existingFlashSale.getUsedQuantity());
        
        FlashSale updatedFlashSale = builder.build();
        
        // Validate time range if times are being updated
        if (updatedFlashSale.getEndTime().isBefore(updatedFlashSale.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        flashSaleMapper.updateFlashSale(updatedFlashSale);
        
        log.info("Updated flash sale {}", id);
        return getFlashSaleById(id);
    }
    
    @Override
    @Transactional
    public void deleteFlashSale(Long id) {
        FlashSale flashSale = flashSaleMapper.findById(id);
        if (flashSale == null) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        
        if (flashSale.isCurrentlyActive()) {
            throw new IllegalArgumentException("Cannot delete an active flash sale");
        }
        
        flashSaleMapper.deleteFlashSaleProducts(id);
        flashSaleMapper.deleteFlashSale(id);
        
        log.info("Deleted flash sale {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FlashSaleResponse getFlashSaleById(Long id) {
        FlashSale flashSale = flashSaleMapper.findByIdWithCreator(id);
        if (flashSale == null) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        
        List<FlashSaleProduct> products = flashSaleMapper.findProductsByFlashSaleId(id);
        
        return convertToResponse(flashSale, products);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleResponse> getActiveFlashSales() {
        List<FlashSale> flashSales = flashSaleMapper.findActiveFlashSales();
        return flashSales.stream()
                .map(fs -> convertToResponse(fs, flashSaleMapper.findProductsByFlashSaleId(fs.getId())))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleResponse> getUpcomingFlashSales() {
        List<FlashSale> flashSales = flashSaleMapper.findUpcomingFlashSales();
        return flashSales.stream()
                .map(fs -> convertToResponse(fs, flashSaleMapper.findProductsByFlashSaleId(fs.getId())))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<FlashSaleResponse> getFlashSalesByCreator(Long createdBy, int page, int size) {
        User creator = userMapper.findById(createdBy)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        int offset = page * size;
        List<FlashSale> flashSales = flashSaleMapper.findByCreator(createdBy, size, offset);
        long totalElements = flashSaleMapper.countByCreator(createdBy);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<FlashSaleResponse> content = flashSales.stream()
                .map(fs -> convertToResponse(fs, flashSaleMapper.findProductsByFlashSaleId(fs.getId())))
                .collect(Collectors.toList());
        
        return PaginationResponse.<FlashSaleResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<FlashSaleResponse> getAllFlashSales(int page, int size) {
        int offset = page * size;
        List<FlashSale> flashSales = flashSaleMapper.findAll(size, offset);
        long totalElements = flashSaleMapper.countAll();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<FlashSaleResponse> content = flashSales.stream()
                .map(fs -> convertToResponse(fs, flashSaleMapper.findProductsByFlashSaleId(fs.getId())))
                .collect(Collectors.toList());
        
        return PaginationResponse.<FlashSaleResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<FlashSaleProductResponse> getActiveFlashSaleProducts(int page, int size) {
        int offset = page * size;
        List<FlashSaleProduct> products = flashSaleMapper.findActiveFlashSaleProducts(size, offset);
        long totalElements = flashSaleMapper.countActiveFlashSaleProducts();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<FlashSaleProductResponse> content = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        
        return PaginationResponse.<FlashSaleProductResponse>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public FlashSaleProductResponse getActiveFlashSaleByProductId(Long productId) {
        FlashSaleProduct flashSaleProduct = flashSaleMapper.findActiveFlashSaleByProductId(productId);
        if (flashSaleProduct == null) {
            return null;
        }
        
        return convertToProductResponse(flashSaleProduct);
    }
    
    @Override
    @Transactional
    public void activateFlashSale(Long id) {
        FlashSale flashSale = flashSaleMapper.findById(id);
        if (flashSale == null) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        
        flashSaleMapper.updateFlashSaleStatus(id, true);
        log.info("Activated flash sale {}", id);
    }
    
    @Override
    @Transactional
    public void deactivateFlashSale(Long id) {
        FlashSale flashSale = flashSaleMapper.findById(id);
        if (flashSale == null) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        
        flashSaleMapper.updateFlashSaleStatus(id, false);
        log.info("Deactivated flash sale {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canPurchaseFlashSaleProduct(Long productId, int quantity) {
        FlashSaleProduct flashSaleProduct = flashSaleMapper.findActiveFlashSaleByProductId(productId);
        if (flashSaleProduct == null) {
            return false;
        }
        
        return flashSaleProduct.hasQuantityAvailable() && 
               flashSaleProduct.getRemainingQuantity() >= quantity;
    }
    
    @Override
    @Transactional
    public void recordFlashSalePurchase(Long productId, int quantity) {
        FlashSaleProduct flashSaleProduct = flashSaleMapper.findActiveFlashSaleByProductId(productId);
        if (flashSaleProduct == null) {
            throw new IllegalArgumentException("No active flash sale for this product");
        }
        
        if (!canPurchaseFlashSaleProduct(productId, quantity)) {
            throw new IllegalArgumentException("Insufficient flash sale quantity available");
        }
        
        // Update product quantity
        flashSaleMapper.incrementProductUsedQuantity(flashSaleProduct.getId(), quantity);
        
        // Update overall flash sale quantity
        flashSaleMapper.incrementUsedQuantity(flashSaleProduct.getFlashSaleId(), quantity);
        
        log.info("Recorded flash sale purchase: product {} quantity {}", productId, quantity);
    }
    
    private FlashSaleResponse convertToResponse(FlashSale flashSale, List<FlashSaleProduct> products) {
        List<FlashSaleProductResponse> productResponses = products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
        
        return FlashSaleResponse.builder()
                .id(flashSale.getId())
                .name(flashSale.getName())
                .description(flashSale.getDescription())
                .startTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .discountPercentage(flashSale.getDiscountPercentage())
                .maxQuantity(flashSale.getMaxQuantity())
                .usedQuantity(flashSale.getUsedQuantity())
                .isActive(flashSale.getIsActive())
                .createdAt(flashSale.getCreatedAt())
                .updatedAt(flashSale.getUpdatedAt())
                .createdBy(flashSale.getCreatedBy())
                .creatorName(flashSale.getCreatorName())
                .currentlyActive(flashSale.isCurrentlyActive())
                .upcoming(flashSale.isUpcoming())
                .expired(flashSale.isExpired())
                .hasQuantityAvailable(flashSale.hasQuantityAvailable())
                .remainingQuantity(flashSale.getRemainingQuantity())
                .products(productResponses)
                .build();
    }
    
    private FlashSaleProductResponse convertToProductResponse(FlashSaleProduct product) {
        return FlashSaleProductResponse.builder()
                .id(product.getId())
                .flashSaleId(product.getFlashSaleId())
                .productId(product.getProductId())
                .originalPrice(product.getOriginalPrice())
                .salePrice(product.getSalePrice())
                .maxQuantityPerProduct(product.getMaxQuantityPerProduct())
                .usedQuantityPerProduct(product.getUsedQuantityPerProduct())
                .createdAt(product.getCreatedAt())
                .productName(product.getProductName())
                .productSlug(product.getProductSlug())
                .productImage(product.getProductImage())
                .sellerName(product.getSellerName())
                .hasQuantityAvailable(product.hasQuantityAvailable())
                .remainingQuantity(product.getRemainingQuantity())
                .discountAmount(product.getDiscountAmount())
                .discountPercentage(product.getDiscountPercentage())
                .build();
    }
}