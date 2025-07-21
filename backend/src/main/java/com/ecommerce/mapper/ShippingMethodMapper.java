package com.ecommerce.mapper;

import com.ecommerce.model.ShippingMethod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ShippingMethodMapper {
    
    // Basic CRUD operations
    void insert(ShippingMethod shippingMethod);
    void update(ShippingMethod shippingMethod);
    void deleteById(@Param("id") Long id);
    Optional<ShippingMethod> findById(@Param("id") Long id);
    List<ShippingMethod> findAll();
    
    // Active shipping methods
    List<ShippingMethod> findActiveShippingMethods();
    
    // Status management
    void updateStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);
    
    // Check existence
    boolean existsById(@Param("id") Long id);
}