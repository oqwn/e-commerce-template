package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.PaginationResponse;
import com.ecommerce.dto.request.CreateFlashSaleRequest;
import com.ecommerce.dto.request.UpdateFlashSaleRequest;
import com.ecommerce.dto.response.FlashSaleResponse;
import com.ecommerce.dto.response.FlashSaleProductResponse;
import com.ecommerce.service.FlashSaleService;
import com.ecommerce.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flash-sales")
@RequiredArgsConstructor
@Tag(name = "Flash Sales", description = "Flash sales management APIs")
@SecurityRequirement(name = "bearerAuth")
public class FlashSaleController {
    
    private final FlashSaleService flashSaleService;
    
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Create flash sale", description = "Creates a new flash sale with products")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Flash sale created",
            content = @Content(schema = @Schema(implementation = FlashSaleResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse> createFlashSale(@Valid @RequestBody CreateFlashSaleRequest request) {
        Long createdBy = SecurityUtils.getCurrentUserId();
        FlashSaleResponse response = flashSaleService.createFlashSale(createdBy, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flash sale created successfully", response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Update flash sale", description = "Updates an existing flash sale")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flash sale not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot update active flash sale")
    })
    public ResponseEntity<ApiResponse> updateFlashSale(
            @Parameter(description = "Flash sale ID") @PathVariable Long id,
            @Valid @RequestBody UpdateFlashSaleRequest request) {
        
        FlashSaleResponse response = flashSaleService.updateFlashSale(id, request);
        return ResponseEntity.ok(ApiResponse.success("Flash sale updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Delete flash sale", description = "Deletes a flash sale and its products")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flash sale not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cannot delete active flash sale")
    })
    public ResponseEntity<ApiResponse> deleteFlashSale(
            @Parameter(description = "Flash sale ID") @PathVariable Long id) {
        
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.ok(ApiResponse.success("Flash sale deleted successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get flash sale", description = "Retrieves a flash sale by ID with all details")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flash sale not found")
    })
    public ResponseEntity<ApiResponse> getFlashSale(
            @Parameter(description = "Flash sale ID") @PathVariable Long id) {
        
        FlashSaleResponse response = flashSaleService.getFlashSaleById(id);
        return ResponseEntity.ok(ApiResponse.success("Flash sale retrieved successfully", response));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active flash sales", description = "Retrieves all currently active flash sales")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active flash sales retrieved")
    })
    public ResponseEntity<ApiResponse> getActiveFlashSales() {
        List<FlashSaleResponse> response = flashSaleService.getActiveFlashSales();
        return ResponseEntity.ok(ApiResponse.success("Active flash sales retrieved", response));
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming flash sales", description = "Retrieves all upcoming flash sales")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Upcoming flash sales retrieved")
    })
    public ResponseEntity<ApiResponse> getUpcomingFlashSales() {
        List<FlashSaleResponse> response = flashSaleService.getUpcomingFlashSales();
        return ResponseEntity.ok(ApiResponse.success("Upcoming flash sales retrieved", response));
    }
    
    @GetMapping("/my-flash-sales")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's flash sales", description = "Retrieves flash sales created by the current user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User flash sales retrieved")
    })
    public ResponseEntity<ApiResponse> getMyFlashSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long createdBy = SecurityUtils.getCurrentUserId();
        PaginationResponse<FlashSaleResponse> response = flashSaleService.getFlashSalesByCreator(createdBy, page, size);
        return ResponseEntity.ok(ApiResponse.success("User flash sales retrieved", response));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all flash sales", description = "Retrieves all flash sales (admin only)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All flash sales retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ApiResponse> getAllFlashSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PaginationResponse<FlashSaleResponse> response = flashSaleService.getAllFlashSales(page, size);
        return ResponseEntity.ok(ApiResponse.success("All flash sales retrieved", response));
    }
    
    @GetMapping("/products")
    @Operation(summary = "Get active flash sale products", description = "Retrieves all products currently in active flash sales")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active flash sale products retrieved")
    })
    public ResponseEntity<ApiResponse> getActiveFlashSaleProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        PaginationResponse<FlashSaleProductResponse> response = flashSaleService.getActiveFlashSaleProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success("Active flash sale products retrieved", response));
    }
    
    @GetMapping("/products/{productId}")
    @Operation(summary = "Get flash sale by product", description = "Retrieves active flash sale information for a specific product")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale product retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No active flash sale for product")
    })
    public ResponseEntity<ApiResponse> getFlashSaleByProduct(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        
        FlashSaleProductResponse response = flashSaleService.getActiveFlashSaleByProductId(productId);
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success("No active flash sale for this product", null));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Flash sale product retrieved", response));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Activate flash sale", description = "Activates a flash sale")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale activated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flash sale not found")
    })
    public ResponseEntity<ApiResponse> activateFlashSale(
            @Parameter(description = "Flash sale ID") @PathVariable Long id) {
        
        flashSaleService.activateFlashSale(id);
        return ResponseEntity.ok(ApiResponse.success("Flash sale activated successfully"));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Deactivate flash sale", description = "Deactivates a flash sale")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Flash sale deactivated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Flash sale not found")
    })
    public ResponseEntity<ApiResponse> deactivateFlashSale(
            @Parameter(description = "Flash sale ID") @PathVariable Long id) {
        
        flashSaleService.deactivateFlashSale(id);
        return ResponseEntity.ok(ApiResponse.success("Flash sale deactivated successfully"));
    }
    
    @PostMapping("/products/{productId}/purchase")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Record flash sale purchase", description = "Records a purchase from a flash sale (used internally)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Purchase recorded"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient quantity or no active flash sale")
    })
    public ResponseEntity<ApiResponse> recordFlashSalePurchase(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @RequestParam int quantity) {
        
        flashSaleService.recordFlashSalePurchase(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Flash sale purchase recorded"));
    }
}