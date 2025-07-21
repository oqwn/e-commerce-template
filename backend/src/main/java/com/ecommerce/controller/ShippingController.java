package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.ShippingMethodResponse;
import com.ecommerce.service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@CrossOrigin(origins = "*")
public class ShippingController {
    
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @GetMapping("/methods")
    public ResponseEntity<ApiResponse> getAvailableShippingMethods() {
        try {
            List<ShippingMethodResponse> methods = shippingService.getAvailableShippingMethods();
            return ResponseEntity.ok(ApiResponse.success("Shipping methods retrieved successfully", methods));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse> calculateShipping(
            @RequestParam Long methodId,
            @RequestParam(required = false) String zipCode,
            @RequestParam(required = false) Double weight) {
        try {
            ShippingMethodResponse calculation = shippingService.calculateShippingCost(methodId, zipCode, weight);
            return ResponseEntity.ok(ApiResponse.success("Shipping cost calculated", calculation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}