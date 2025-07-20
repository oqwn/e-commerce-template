package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.model.User;
import com.ecommerce.security.JwtTokenProvider;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;
    
    public AuthController(UserService userService, JwtTokenProvider tokenProvider, EmailService emailService) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole()
            );
            
            logger.info("User registered successfully: {}", user.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success(
                "Registration successful. Please check your email to verify your account."
            ));
            
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid email or password"));
            }
            
            // Generate tokens
            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);
            
            AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                tokenProvider.getExpirationTime(),
                user
            );
            
            logger.info("User logged in successfully: {}", user.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
            
        } catch (RuntimeException e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid refresh token"));
            }
            
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);
            User user = userService.findById(userId);
            
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not found"));
            }
            
            // Generate new tokens
            String newAccessToken = tokenProvider.generateAccessToken(user);
            String newRefreshToken = tokenProvider.generateRefreshToken(user);
            
            AuthResponse authResponse = new AuthResponse(
                newAccessToken,
                newRefreshToken,
                tokenProvider.getExpirationTime(),
                user
            );
            
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
            
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Token refresh failed"));
        }
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = userService.verifyEmail(token);
            
            if (verified) {
                // Send welcome email after verification
                try {
                    User user = userService.findById(tokenProvider.getUserIdFromToken(token));
                    if (user != null) {
                        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
                    }
                } catch (Exception e) {
                    // Don't fail verification if welcome email fails
                    logger.warn("Failed to send welcome email", e);
                }
                
                return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired verification token"));
            }
            
        } catch (Exception e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Email verification failed"));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            userService.resendVerificationEmail(request.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success(
                "Verification email sent. Please check your email."
            ));
            
        } catch (RuntimeException e) {
            logger.error("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.initiatePasswordReset(request.getEmail());
            
            // Always return success for security (don't reveal if email exists)
            return ResponseEntity.ok(ApiResponse.success(
                "If an account with that email exists, a password reset link has been sent."
            ));
            
        } catch (Exception e) {
            logger.error("Password reset initiation failed: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(
                "If an account with that email exists, a password reset link has been sent."
            ));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean reset = userService.resetPassword(request.getToken(), request.getNewPassword());
            
            if (reset) {
                return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired reset token"));
            }
            
        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Password reset failed"));
        }
    }
    
    // Helper DTOs
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
    
    public static class ResendVerificationRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class ForgotPasswordRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}