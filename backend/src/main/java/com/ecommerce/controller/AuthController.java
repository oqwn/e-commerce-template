package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.model.User;
import com.ecommerce.security.JwtTokenProvider;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
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
    @Operation(summary = "Register a new user", description = "Creates a new user account and sends email verification")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole()
            );
            
            logger.info("User registered successfully: {}", user.getEmail());
            
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(
                "Registration successful. Please check your email to verify your account."
            ));
            
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = com.ecommerce.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.ecommerce.dto.ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());
            
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            logger.info("Authentication result for {}: {}", request.getEmail(), user != null ? "SUCCESS" : "FAILED");
            
            if (user == null) {
                logger.warn("Authentication failed for email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error("Invalid email or password"));
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
            
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Login successful", authResponse));
            
        } catch (RuntimeException e) {
            logger.error("Login failed with RuntimeException: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Login failed with unexpected exception: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error("Login failed"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error("Invalid refresh token"));
            }
            
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);
            User user = userService.findById(userId);
            
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error("User not found"));
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
            
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Token refreshed successfully", authResponse));
            
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error("Token refresh failed"));
        }
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> verifyEmail(@RequestParam String token) {
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
                
                return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Email verified successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error("Invalid or expired verification token"));
            }
            
        } catch (Exception e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error("Email verification failed"));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            userService.resendVerificationEmail(request.getEmail());
            
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(
                "Verification email sent. Please check your email."
            ));
            
        } catch (RuntimeException e) {
            logger.error("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            userService.initiatePasswordReset(request.getEmail());
            
            // Always return success for security (don't reveal if email exists)
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(
                "If an account with that email exists, a password reset link has been sent."
            ));
            
        } catch (Exception e) {
            logger.error("Password reset initiation failed: {}", e.getMessage());
            return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success(
                "If an account with that email exists, a password reset link has been sent."
            ));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<com.ecommerce.dto.ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean reset = userService.resetPassword(request.getToken(), request.getNewPassword());
            
            if (reset) {
                return ResponseEntity.ok(com.ecommerce.dto.ApiResponse.success("Password reset successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(com.ecommerce.dto.ApiResponse.error("Invalid or expired reset token"));
            }
            
        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(com.ecommerce.dto.ApiResponse.error("Password reset failed"));
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