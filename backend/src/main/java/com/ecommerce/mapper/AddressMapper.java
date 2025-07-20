package com.ecommerce.mapper;

import com.ecommerce.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AddressMapper {
    
    // Basic CRUD operations
    void insert(Address address);
    void update(Address address);
    void deleteById(@Param("id") Long id);
    Optional<Address> findById(@Param("id") Long id);
    
    // User address operations
    List<Address> findByUserId(@Param("userId") Long userId);
    Optional<Address> findDefaultByUserId(@Param("userId") Long userId);
    void deleteByUserId(@Param("userId") Long userId);
    
    // Default address management
    void clearDefaultForUser(@Param("userId") Long userId);
    void setAsDefault(@Param("id") Long id, @Param("userId") Long userId);
    
    // Address type operations
    List<Address> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Address.AddressType type);
    
    // Validation
    boolean existsByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);
    long countByUserId(@Param("userId") Long userId);
}