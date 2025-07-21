package com.ecommerce.service;

import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.model.User;
import com.ecommerce.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userMapper.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return UserPrincipal.create(userOpt.get());
    }
    
    public UserDetails loadUserById(Long id) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }
        return UserPrincipal.create(userOpt.get());
    }
    
    public User registerUser(String email, String password, String firstName, String lastName, User.UserRole role) {
        // Check if user already exists
        if (userMapper.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = new User(email, passwordEncoder.encode(password), firstName, lastName, role);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEmailVerified(false);
        
        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));
        
        userMapper.insert(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);
        
        return user;
    }
    
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userMapper.findByEmailVerificationToken(token);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        userMapper.verifyEmail(user.getId());
        return true;
    }
    
    public void resendVerificationEmail(String email) {
        Optional<User> userOpt = userMapper.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        if (user.isEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }
        
        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);
        
        userMapper.updateEmailVerificationToken(user.getId(), verificationToken, expiry);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);
    }
    
    public User authenticateUser(String email, String password) {
        Optional<User> userOpt = userMapper.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("DEBUG: User not found for email: " + email);
            return null;
        }
        
        User user = userOpt.get();
        System.out.println("DEBUG: User found: " + user.getEmail() + ", status: " + user.getStatus());
        System.out.println("DEBUG: Password hash: " + (user.getPasswordHash() != null ? "present" : "null"));
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            System.out.println("DEBUG: Password does not match");
            return null;
        }
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            System.out.println("DEBUG: User status is not active: " + user.getStatus());
            throw new RuntimeException("Account is not active");
        }
        
        System.out.println("DEBUG: Authentication successful");
        return user;
    }
    
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userMapper.findByEmail(email);
        if (userOpt.isEmpty()) {
            // For security, don't reveal if email exists
            return;
        }
        
        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1); // 1 hour expiry
        
        userMapper.updatePasswordResetToken(user.getId(), resetToken, expiry);
        
        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
    }
    
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userMapper.findByPasswordResetToken(token);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(user.getId(), encodedPassword);
        userMapper.clearPasswordResetToken(user.getId());
        
        return true;
    }
    
    public User findById(Long id) {
        return userMapper.findById(id).orElse(null);
    }
    
    public User findByEmail(String email) {
        return userMapper.findByEmail(email).orElse(null);
    }
    
    public User findByIdWithAddresses(Long id) {
        return userMapper.findByIdWithAddresses(id).orElse(null);
    }
    
    public void updateProfile(Long userId, String firstName, String lastName, String phone) {
        userMapper.updateProfile(userId, firstName, lastName, phone);
    }
    
    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        userMapper.updateProfile(user.getId(), request.getFirstName(), request.getLastName(), request.getPhone());
        
        // Return updated user
        return findById(user.getId());
    }
    
    public User updateStatus(Long id, String status) {
        User user = findById(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        try {
            User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
            updateUserStatus(id, userStatus);
            return findById(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }
    
    public void deleteUser(Long id) {
        User user = findById(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        userMapper.deleteById(id);
    }
    
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userMapper.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, encodedPassword);
    }
    
    public void updateUserRole(Long userId, User.UserRole role) {
        userMapper.updateRole(userId, role);
    }
    
    public void updateUserStatus(Long userId, User.UserStatus status) {
        userMapper.updateStatus(userId, status);
    }
    
    public List<User> findAllUsers() {
        return userMapper.findAll();
    }
    
    public List<User> findUsersByRole(User.UserRole role) {
        return userMapper.findByRole(role);
    }
    
    public List<User> findUsersByStatus(User.UserStatus status) {
        return userMapper.findByStatus(status);
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userMapper.searchByName(searchTerm);
    }
    
    public long getTotalUserCount() {
        return userMapper.countAll();
    }
    
    public long getUserCountByRole(User.UserRole role) {
        return userMapper.countByRole(role);
    }
    
    public long getUserCountByStatus(User.UserStatus status) {
        return userMapper.countByStatus(status);
    }
    
    public void deleteUnverifiedUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<User> unverifiedUsers = userMapper.findUnverifiedUsers(cutoff);
        
        for (User user : unverifiedUsers) {
            userMapper.deleteById(user.getId());
        }
    }
}