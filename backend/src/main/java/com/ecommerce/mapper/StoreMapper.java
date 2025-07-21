package com.ecommerce.mapper;

import com.ecommerce.model.Store;
import com.ecommerce.model.StoreAnalytics;
import com.ecommerce.model.StoreCustomization;
import com.ecommerce.model.StoreOperatingHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface StoreMapper {
    
    // Basic CRUD operations
    void insert(Store store);
    void update(Store store);
    void deleteById(Long id);
    Optional<Store> findById(Long id);
    Optional<Store> findByIdWithDetails(Long id);
    Optional<Store> findBySellerId(Long sellerId);
    Optional<Store> findBySlug(String slug);
    List<Store> findAll();
    
    // Store status management
    void updateStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);
    void updateVerificationStatus(
        @Param("id") Long id, 
        @Param("status") Store.VerificationStatus status,
        @Param("verificationDate") java.time.LocalDateTime verificationDate
    );
    
    // Store metrics
    void updateMetrics(
        @Param("id") Long id,
        @Param("rating") java.math.BigDecimal rating,
        @Param("totalReviews") Integer totalReviews,
        @Param("totalProducts") Integer totalProducts,
        @Param("totalSales") Integer totalSales
    );
    
    // Search and filtering
    List<Store> findActiveStores();
    List<Store> findVerifiedStores();
    List<Store> findByCategory(@Param("categoryId") Long categoryId);
    List<Store> searchByName(@Param("searchTerm") String searchTerm);
    List<Store> findTopRatedStores(@Param("limit") int limit);
    
    // Store customization
    void insertCustomization(StoreCustomization customization);
    void updateCustomization(StoreCustomization customization);
    Optional<StoreCustomization> findCustomizationByStoreId(Long storeId);
    
    // Operating hours
    void insertOperatingHours(StoreOperatingHours hours);
    void updateOperatingHours(StoreOperatingHours hours);
    void deleteOperatingHours(Long id);
    List<StoreOperatingHours> findOperatingHoursByStoreId(Long storeId);
    
    // Analytics
    void insertAnalytics(StoreAnalytics analytics);
    void updateAnalytics(StoreAnalytics analytics);
    Optional<StoreAnalytics> findAnalyticsByStoreAndDate(
        @Param("storeId") Long storeId,
        @Param("date") LocalDate date
    );
    List<StoreAnalytics> findAnalyticsByStoreAndDateRange(
        @Param("storeId") Long storeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Statistics
    Long countAll();
    Long countActive();
    Long countVerified();
    Long countByVerificationStatus(Store.VerificationStatus status);
    
    // Check existence
    Boolean existsBySlug(String slug);
    Boolean existsBySellerId(Long sellerId);
}