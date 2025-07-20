package com.ecommerce.controller;

import com.ecommerce.dto.UserProfileDto;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import com.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserProfileDto.from(user));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User updatedUser = userService.updateProfile(email, request);
        return ResponseEntity.ok(UserProfileDto.from(updatedUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserProfileDto.from(user));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        User user = userService.updateStatus(id, status);
        return ResponseEntity.ok(UserProfileDto.from(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}