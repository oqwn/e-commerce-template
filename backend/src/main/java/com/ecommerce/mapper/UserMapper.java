package com.ecommerce.mapper;

import com.ecommerce.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    
    // Basic CRUD operations
    void insert(User user);
    void update(User user);
    void deleteById(@Param("id") Long id);
    Optional<User> findById(@Param("id") Long id);
    List<User> findAll();
    
    // Authentication related queries
    Optional<User> findByEmail(@Param("email") String email);
    Optional<User> findByEmailVerificationToken(@Param("token") String token);
    Optional<User> findByPasswordResetToken(@Param("token") String token);
    
    // Email verification
    void updateEmailVerificationToken(@Param("id") Long id, @Param("token") String token, @Param("expiry") LocalDateTime expiry);
    void verifyEmail(@Param("id") Long id);
    
    // Password reset
    void updatePasswordResetToken(@Param("id") Long id, @Param("token") String token, @Param("expiry") LocalDateTime expiry);
    void updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);
    void clearPasswordResetToken(@Param("id") Long id);
    
    // User management
    void updateStatus(@Param("id") Long id, @Param("status") User.UserStatus status);
    void updateRole(@Param("id") Long id, @Param("role") User.UserRole role);
    void updateProfile(@Param("id") Long id, @Param("firstName") String firstName, 
                      @Param("lastName") String lastName, @Param("phone") String phone);
    
    // Search and filtering
    List<User> findByRole(@Param("role") User.UserRole role);
    List<User> findByStatus(@Param("status") User.UserStatus status);
    List<User> searchByName(@Param("searchTerm") String searchTerm);
    List<User> findUnverifiedUsers(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    // Statistics
    long countAll();
    long countByRole(@Param("role") User.UserRole role);
    long countByStatus(@Param("status") User.UserStatus status);
    long countRegisteredAfter(@Param("date") LocalDateTime date);
    
    // User with addresses (for profile management)
    Optional<User> findByIdWithAddresses(@Param("id") Long id);
    
    // Check existence
    boolean existsByEmail(@Param("email") String email);
    boolean existsById(@Param("id") Long id);
}