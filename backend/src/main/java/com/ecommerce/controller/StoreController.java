package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.model.Store;
import com.ecommerce.model.StoreAnalytics;
import com.ecommerce.model.StoreCustomization;
import com.ecommerce.service.StoreService;
import com.ecommerce.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {
    
    private final StoreService storeService;
    
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            Store store = storeService.createStore(sellerId, request);
            return ResponseEntity.ok(ApiResponse.success("Store created successfully", store));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/my-store")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getMyStore() {
        Long sellerId = SecurityUtils.getCurrentUserId();
        Store store = storeService.findBySellerId(sellerId);
        
        if (store == null) {
            return ResponseEntity.ok(ApiResponse.success("No store found", null));
        }
        
        Store storeWithDetails = storeService.findByIdWithDetails(store.getId());
        return ResponseEntity.ok(ApiResponse.success("Store retrieved successfully", storeWithDetails));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStore(@PathVariable Long id) {
        Store store = storeService.findByIdWithDetails(id);
        
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ApiResponse.success("Store retrieved successfully", store));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse> getStoreBySlug(@PathVariable String slug) {
        Store store = storeService.findBySlug(slug);
        
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        
        Store storeWithDetails = storeService.findByIdWithDetails(store.getId());
        return ResponseEntity.ok(ApiResponse.success("Store retrieved successfully", storeWithDetails));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> updateStore(
            @PathVariable Long id,
            @RequestBody UpdateStoreRequest request) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            Store store = storeService.updateStore(id, sellerId, request);
            return ResponseEntity.ok(ApiResponse.success("Store updated successfully", store));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/logo")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            String logoUrl = storeService.uploadStoreLogo(id, sellerId, file);
            return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully", logoUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/banner")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> uploadBanner(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            String bannerUrl = storeService.uploadStoreBanner(id, sellerId, file);
            return ResponseEntity.ok(ApiResponse.success("Banner uploaded successfully", bannerUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/customization")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> updateCustomization(
            @PathVariable Long id,
            @RequestBody UpdateStoreCustomizationRequest request) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            StoreCustomization customization = storeService.updateCustomization(id, sellerId, request);
            return ResponseEntity.ok(ApiResponse.success("Customization updated successfully", customization));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/operating-hours")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> updateOperatingHours(
            @PathVariable Long id,
            @RequestBody List<UpdateOperatingHoursRequest> requests) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            storeService.updateOperatingHours(id, sellerId, requests);
            return ResponseEntity.ok(ApiResponse.success("Operating hours updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/analytics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getAnalytics(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            StoreAnalytics analytics = storeService.getAnalytics(id, sellerId, date);
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/analytics/range")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getAnalyticsRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            List<StoreAnalytics> analytics = storeService.getAnalyticsRange(id, sellerId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse> listStores(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "active") String status) {
        
        List<Store> stores;
        
        if (search != null && !search.trim().isEmpty()) {
            stores = storeService.searchStores(search);
        } else if (categoryId != null) {
            stores = storeService.findByCategory(categoryId);
        } else if ("verified".equals(status)) {
            stores = storeService.findVerifiedStores();
        } else {
            stores = storeService.findActiveStores();
        }
        
        return ResponseEntity.ok(ApiResponse.success("Stores retrieved successfully", stores));
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse> getTopRatedStores(@RequestParam(defaultValue = "10") int limit) {
        List<Store> stores = storeService.findTopRatedStores(limit);
        return ResponseEntity.ok(ApiResponse.success("Top rated stores retrieved successfully", stores));
    }
    
    // Admin endpoints
    @PutMapping("/{id}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateVerificationStatus(
            @PathVariable Long id,
            @RequestParam Store.VerificationStatus status) {
        try {
            Long reviewerId = SecurityUtils.getCurrentUserId();
            storeService.updateVerificationStatus(id, status, reviewerId);
            return ResponseEntity.ok(ApiResponse.success("Verification status updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateStoreStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        try {
            storeService.updateStoreStatus(id, isActive);
            return ResponseEntity.ok(ApiResponse.success("Store status updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getStoreStats() {
        StoreStatsResponse stats = storeService.getStoreStats();
        return ResponseEntity.ok(ApiResponse.success("Store statistics retrieved successfully", stats));
    }
    
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse> getMyStoreAnalytics(
            @RequestParam(required = false, defaultValue = "last7days") String range) {
        try {
            Long sellerId = SecurityUtils.getCurrentUserId();
            Store store = storeService.findBySellerId(sellerId);
            
            if (store == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Store not found"));
            }
            
            LocalDate endDate = LocalDate.now();
            LocalDate startDate;
            
            switch (range) {
                case "last30days":
                    startDate = endDate.minusDays(30);
                    break;
                case "last90days":
                    startDate = endDate.minusDays(90);
                    break;
                default:
                    startDate = endDate.minusDays(7);
            }
            
            List<StoreAnalytics> analytics = storeService.getAnalyticsRange(store.getId(), sellerId, startDate, endDate);
            
            // Calculate summary stats
            AnalyticsSummaryResponse summary = storeService.calculateAnalyticsSummary(analytics, startDate, endDate);
            
            StoreAnalyticsResponse response = new StoreAnalyticsResponse();
            response.setAnalytics(analytics);
            response.setSummary(summary);
            
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}