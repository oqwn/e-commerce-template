package com.ecommerce.dto;

import java.util.List;

public class CreateOrderRequest {
    private Long shippingAddressId;
    private Long billingAddressId;
    private Long shippingMethodId;
    private String paymentMethod;
    private String notes;
    private String couponCode;
    private List<OrderItemRequest> items;
    
    // Constructors
    public CreateOrderRequest() {}
    
    public CreateOrderRequest(Long shippingAddressId, Long billingAddressId, 
                             Long shippingMethodId, String paymentMethod) {
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.shippingMethodId = shippingMethodId;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    
    public Long getBillingAddressId() { return billingAddressId; }
    public void setBillingAddressId(Long billingAddressId) { this.billingAddressId = billingAddressId; }
    
    public Long getShippingMethodId() { return shippingMethodId; }
    public void setShippingMethodId(Long shippingMethodId) { this.shippingMethodId = shippingMethodId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        private java.util.Map<String, Object> selectedVariants;
        
        // Constructors
        public OrderItemRequest() {}
        
        public OrderItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public java.util.Map<String, Object> getSelectedVariants() { return selectedVariants; }
        public void setSelectedVariants(java.util.Map<String, Object> selectedVariants) { this.selectedVariants = selectedVariants; }
    }
}