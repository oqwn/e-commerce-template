package com.ecommerce.service;

import com.ecommerce.dto.request.CreateFlashSaleRequest;
import com.ecommerce.dto.request.UpdateFlashSaleRequest;
import com.ecommerce.dto.response.FlashSaleResponse;
import com.ecommerce.dto.response.FlashSaleProductResponse;
import com.ecommerce.dto.PaginationResponse;

import java.util.List;

public interface FlashSaleService {
    FlashSaleResponse createFlashSale(Long createdBy, CreateFlashSaleRequest request);
    FlashSaleResponse updateFlashSale(Long id, UpdateFlashSaleRequest request);
    void deleteFlashSale(Long id);
    FlashSaleResponse getFlashSaleById(Long id);
    List<FlashSaleResponse> getActiveFlashSales();
    List<FlashSaleResponse> getUpcomingFlashSales();
    PaginationResponse<FlashSaleResponse> getFlashSalesByCreator(Long createdBy, int page, int size);
    PaginationResponse<FlashSaleResponse> getAllFlashSales(int page, int size);
    PaginationResponse<FlashSaleProductResponse> getActiveFlashSaleProducts(int page, int size);
    FlashSaleProductResponse getActiveFlashSaleByProductId(Long productId);
    void activateFlashSale(Long id);
    void deactivateFlashSale(Long id);
    boolean canPurchaseFlashSaleProduct(Long productId, int quantity);
    void recordFlashSalePurchase(Long productId, int quantity);
}