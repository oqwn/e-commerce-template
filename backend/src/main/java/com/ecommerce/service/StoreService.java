package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.mapper.StoreMapper;
import com.ecommerce.model.*;
import com.ecommerce.util.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class StoreService {
    
    private final StoreMapper storeMapper;
    private final FileUploadService fileUploadService;
    
    public StoreService(StoreMapper storeMapper, FileUploadService fileUploadService) {
        this.storeMapper = storeMapper;
        this.fileUploadService = fileUploadService;
    }
    
    public Store createStore(Long sellerId, CreateStoreRequest request) {
        // Check if seller already has a store
        if (storeMapper.existsBySellerId(sellerId)) {
            throw new RuntimeException("Seller already has a store");
        }
        
        // Generate unique slug
        String baseSlug = SlugUtils.generateSlug(request.getStoreName());
        String slug = baseSlug;
        int counter = 1;
        while (storeMapper.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        
        // Create store
        Store store = Store.builder()
                .sellerId(sellerId)
                .storeName(request.getStoreName())
                .storeSlug(slug)
                .description(request.getDescription())
                .businessName(request.getBusinessName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "US")
                .currency("USD")
                .timezone("UTC")
                .isActive(true)
                .isVerified(false)
                .verificationStatus(Store.VerificationStatus.PENDING)
                .rating(BigDecimal.ZERO)
                .totalReviews(0)
                .totalProducts(0)
                .totalSales(0)
                .build();
        
        storeMapper.insert(store);
        
        // Create default customization
        StoreCustomization customization = StoreCustomization.builder()
                .storeId(store.getId())
                .themeName("default")
                .primaryColor("#3B82F6")
                .secondaryColor("#10B981")
                .accentColor("#F59E0B")
                .backgroundColor("#FFFFFF")
                .textColor("#1F2937")
                .layoutType(StoreCustomization.LayoutType.GRID)
                .productsPerPage(20)
                .showBanner(true)
                .showFeaturedProducts(true)
                .showCategories(true)
                .build();
        
        storeMapper.insertCustomization(customization);
        
        // Create default operating hours (9 AM to 6 PM, Monday to Saturday)
        for (int day = 0; day <= 6; day++) {
            StoreOperatingHours hours = StoreOperatingHours.builder()
                    .storeId(store.getId())
                    .dayOfWeek(day)
                    .openTime(day == 0 ? null : java.time.LocalTime.of(9, 0))
                    .closeTime(day == 0 ? null : java.time.LocalTime.of(18, 0))
                    .isClosed(day == 0) // Sunday closed
                    .build();
            
            storeMapper.insertOperatingHours(hours);
        }
        
        return store;
    }
    
    public Store updateStore(Long storeId, Long sellerId, UpdateStoreRequest request) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to update this store");
        }
        
        // Update store fields
        if (request.getStoreName() != null) {
            store.setStoreName(request.getStoreName());
        }
        if (request.getDescription() != null) {
            store.setDescription(request.getDescription());
        }
        if (request.getBusinessName() != null) {
            store.setBusinessName(request.getBusinessName());
        }
        if (request.getContactEmail() != null) {
            store.setContactEmail(request.getContactEmail());
        }
        if (request.getContactPhone() != null) {
            store.setContactPhone(request.getContactPhone());
        }
        if (request.getSupportEmail() != null) {
            store.setSupportEmail(request.getSupportEmail());
        }
        if (request.getSupportPhone() != null) {
            store.setSupportPhone(request.getSupportPhone());
        }
        if (request.getStreet() != null) {
            store.setStreet(request.getStreet());
        }
        if (request.getCity() != null) {
            store.setCity(request.getCity());
        }
        if (request.getState() != null) {
            store.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            store.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            store.setCountry(request.getCountry());
        }
        if (request.getReturnPolicy() != null) {
            store.setReturnPolicy(request.getReturnPolicy());
        }
        if (request.getShippingPolicy() != null) {
            store.setShippingPolicy(request.getShippingPolicy());
        }
        if (request.getPrivacyPolicy() != null) {
            store.setPrivacyPolicy(request.getPrivacyPolicy());
        }
        if (request.getTermsAndConditions() != null) {
            store.setTermsAndConditions(request.getTermsAndConditions());
        }
        if (request.getWebsiteUrl() != null) {
            store.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getFacebookUrl() != null) {
            store.setFacebookUrl(request.getFacebookUrl());
        }
        if (request.getInstagramUrl() != null) {
            store.setInstagramUrl(request.getInstagramUrl());
        }
        if (request.getTwitterUrl() != null) {
            store.setTwitterUrl(request.getTwitterUrl());
        }
        if (request.getYoutubeUrl() != null) {
            store.setYoutubeUrl(request.getYoutubeUrl());
        }
        
        storeMapper.update(store);
        
        return store;
    }
    
    public String uploadStoreLogo(Long storeId, Long sellerId, MultipartFile file) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to update this store");
        }
        
        try {
            String logoUrl = fileUploadService.uploadStoreImage(file);
            store.setLogoUrl(logoUrl);
            storeMapper.update(store);
            return logoUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload logo: " + e.getMessage());
        }
    }
    
    public String uploadStoreBanner(Long storeId, Long sellerId, MultipartFile file) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to update this store");
        }
        
        try {
            String bannerUrl = fileUploadService.uploadStoreImage(file);
            store.setBannerUrl(bannerUrl);
            storeMapper.update(store);
            return bannerUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload banner: " + e.getMessage());
        }
    }
    
    public StoreCustomization updateCustomization(Long storeId, Long sellerId, UpdateStoreCustomizationRequest request) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to update this store");
        }
        
        StoreCustomization customization = storeMapper.findCustomizationByStoreId(storeId)
                .orElseThrow(() -> new RuntimeException("Store customization not found"));
        
        // Update customization fields
        if (request.getThemeName() != null) {
            customization.setThemeName(request.getThemeName());
        }
        if (request.getPrimaryColor() != null) {
            customization.setPrimaryColor(request.getPrimaryColor());
        }
        if (request.getSecondaryColor() != null) {
            customization.setSecondaryColor(request.getSecondaryColor());
        }
        if (request.getAccentColor() != null) {
            customization.setAccentColor(request.getAccentColor());
        }
        if (request.getBackgroundColor() != null) {
            customization.setBackgroundColor(request.getBackgroundColor());
        }
        if (request.getTextColor() != null) {
            customization.setTextColor(request.getTextColor());
        }
        if (request.getLayoutType() != null) {
            customization.setLayoutType(request.getLayoutType());
        }
        if (request.getProductsPerPage() != null) {
            customization.setProductsPerPage(request.getProductsPerPage());
        }
        if (request.getShowBanner() != null) {
            customization.setShowBanner(request.getShowBanner());
        }
        if (request.getShowFeaturedProducts() != null) {
            customization.setShowFeaturedProducts(request.getShowFeaturedProducts());
        }
        if (request.getShowCategories() != null) {
            customization.setShowCategories(request.getShowCategories());
        }
        if (request.getCustomCss() != null) {
            customization.setCustomCss(request.getCustomCss());
        }
        if (request.getMetaTitle() != null) {
            customization.setMetaTitle(request.getMetaTitle());
        }
        if (request.getMetaDescription() != null) {
            customization.setMetaDescription(request.getMetaDescription());
        }
        if (request.getMetaKeywords() != null) {
            customization.setMetaKeywords(request.getMetaKeywords());
        }
        
        storeMapper.updateCustomization(customization);
        
        return customization;
    }
    
    public void updateOperatingHours(Long storeId, Long sellerId, List<UpdateOperatingHoursRequest> requests) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to update this store");
        }
        
        List<StoreOperatingHours> existingHours = storeMapper.findOperatingHoursByStoreId(storeId);
        
        for (UpdateOperatingHoursRequest request : requests) {
            StoreOperatingHours hours = existingHours.stream()
                    .filter(h -> h.getDayOfWeek().equals(request.getDayOfWeek()))
                    .findFirst()
                    .orElse(null);
            
            if (hours != null) {
                hours.setOpenTime(request.getOpenTime());
                hours.setCloseTime(request.getCloseTime());
                hours.setIsClosed(request.getIsClosed());
                storeMapper.updateOperatingHours(hours);
            } else {
                hours = StoreOperatingHours.builder()
                        .storeId(storeId)
                        .dayOfWeek(request.getDayOfWeek())
                        .openTime(request.getOpenTime())
                        .closeTime(request.getCloseTime())
                        .isClosed(request.getIsClosed())
                        .build();
                storeMapper.insertOperatingHours(hours);
            }
        }
    }
    
    public Store findById(Long id) {
        return storeMapper.findById(id).orElse(null);
    }
    
    public Store findByIdWithDetails(Long id) {
        return storeMapper.findByIdWithDetails(id).orElse(null);
    }
    
    public Store findBySellerId(Long sellerId) {
        return storeMapper.findBySellerId(sellerId).orElse(null);
    }
    
    public Store findBySlug(String slug) {
        return storeMapper.findBySlug(slug).orElse(null);
    }
    
    public List<Store> findActiveStores() {
        return storeMapper.findActiveStores();
    }
    
    public List<Store> findVerifiedStores() {
        return storeMapper.findVerifiedStores();
    }
    
    public List<Store> findByCategory(Long categoryId) {
        return storeMapper.findByCategory(categoryId);
    }
    
    public List<Store> searchStores(String searchTerm) {
        return storeMapper.searchByName(searchTerm);
    }
    
    public List<Store> findTopRatedStores(int limit) {
        return storeMapper.findTopRatedStores(limit);
    }
    
    public StoreAnalytics getAnalytics(Long storeId, Long sellerId, LocalDate date) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to view this store's analytics");
        }
        
        return storeMapper.findAnalyticsByStoreAndDate(storeId, date).orElse(null);
    }
    
    public List<StoreAnalytics> getAnalyticsRange(Long storeId, Long sellerId, LocalDate startDate, LocalDate endDate) {
        Store store = storeMapper.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        
        if (!store.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized to view this store's analytics");
        }
        
        return storeMapper.findAnalyticsByStoreAndDateRange(storeId, startDate, endDate);
    }
    
    public void updateVerificationStatus(Long storeId, Store.VerificationStatus status, Long reviewerId) {
        storeMapper.updateVerificationStatus(storeId, status, LocalDateTime.now());
    }
    
    public void updateStoreStatus(Long storeId, boolean isActive) {
        storeMapper.updateStatus(storeId, isActive);
    }
    
    public StoreStatsResponse getStoreStats() {
        return StoreStatsResponse.builder()
                .totalStores(storeMapper.countAll())
                .activeStores(storeMapper.countActive())
                .verifiedStores(storeMapper.countVerified())
                .pendingVerification(storeMapper.countByVerificationStatus(Store.VerificationStatus.PENDING))
                .build();
    }
    
    public AnalyticsSummaryResponse calculateAnalyticsSummary(List<StoreAnalytics> analytics, LocalDate startDate, LocalDate endDate) {
        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();
        
        if (analytics.isEmpty()) {
            summary.setTotalRevenue(BigDecimal.ZERO);
            summary.setTotalOrders(0);
            summary.setAvgOrderValue(BigDecimal.ZERO);
            summary.setConversionRate(0.0);
            summary.setRevenueChange(0.0);
            summary.setOrdersChange(0.0);
            summary.setConversionChange(0.0);
            return summary;
        }
        
        // Calculate current period metrics
        BigDecimal totalRevenue = analytics.stream()
                .map(StoreAnalytics::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalOrders = analytics.stream()
                .mapToInt(StoreAnalytics::getTotalOrders)
                .sum();
        
        BigDecimal avgOrderValue = totalOrders > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        Integer totalVisits = analytics.stream()
                .mapToInt(StoreAnalytics::getTotalVisits)
                .sum();
        
        Double conversionRate = totalVisits > 0
                ? (double) totalOrders / totalVisits * 100
                : 0.0;
        
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalOrders(totalOrders);
        summary.setAvgOrderValue(avgOrderValue);
        summary.setConversionRate(conversionRate);
        
        // Calculate previous period for comparison
        int periodDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate prevEndDate = startDate.minusDays(1);
        LocalDate prevStartDate = prevEndDate.minusDays(periodDays);
        
        List<StoreAnalytics> prevAnalytics = storeMapper.findAnalyticsByStoreAndDateRange(
                analytics.get(0).getStoreId(), prevStartDate, prevEndDate);
        
        if (!prevAnalytics.isEmpty()) {
            BigDecimal prevRevenue = prevAnalytics.stream()
                    .map(StoreAnalytics::getTotalRevenue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Integer prevOrders = prevAnalytics.stream()
                    .mapToInt(StoreAnalytics::getTotalOrders)
                    .sum();
            
            Integer prevVisits = prevAnalytics.stream()
                    .mapToInt(StoreAnalytics::getTotalVisits)
                    .sum();
            
            Double prevConversionRate = prevVisits > 0
                    ? (double) prevOrders / prevVisits * 100
                    : 0.0;
            
            // Calculate percentage changes
            summary.setRevenueChange(prevRevenue.compareTo(BigDecimal.ZERO) > 0
                    ? totalRevenue.subtract(prevRevenue)
                            .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
                    : 0.0);
            
            summary.setOrdersChange(prevOrders > 0
                    ? ((double) totalOrders - prevOrders) / prevOrders * 100
                    : 0.0);
            
            summary.setConversionChange(prevConversionRate > 0
                    ? (conversionRate - prevConversionRate) / prevConversionRate * 100
                    : 0.0);
        } else {
            summary.setRevenueChange(0.0);
            summary.setOrdersChange(0.0);
            summary.setConversionChange(0.0);
        }
        
        return summary;
    }
}