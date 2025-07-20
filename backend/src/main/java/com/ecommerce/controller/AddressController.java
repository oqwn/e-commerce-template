package com.ecommerce.controller;

import com.ecommerce.dto.AddressDto;
import com.ecommerce.dto.CreateAddressRequest;
import com.ecommerce.dto.UpdateAddressRequest;
import com.ecommerce.model.Address;
import com.ecommerce.service.AddressService;
import com.ecommerce.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/me/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressDto>> getCurrentUserAddresses() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Address> addresses = addressService.getUserAddresses(userId);
        List<AddressDto> addressDtos = addresses.stream()
                .map(AddressDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDtos);
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressDto>> getUserAddresses(@PathVariable Long userId) {
        List<Address> addresses = addressService.getUserAddresses(userId);
        List<AddressDto> addressDtos = addresses.stream()
                .map(AddressDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDtos);
    }

    @PostMapping("/me/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDto> addAddressForCurrentUser(@Valid @RequestBody CreateAddressRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Address address = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AddressDto.from(address));
    }

    @PostMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressDto> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody CreateAddressRequest request) {
        Address address = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AddressDto.from(address));
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {
        Address address = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(AddressDto.from(address));
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/addresses/{addressId}/default")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressDto> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        Address address = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(AddressDto.from(address));
    }

    @GetMapping("/{userId}/addresses/default")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressDto> getDefaultAddress(@PathVariable Long userId) {
        Address address = addressService.getDefaultAddress(userId);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AddressDto.from(address));
    }
}