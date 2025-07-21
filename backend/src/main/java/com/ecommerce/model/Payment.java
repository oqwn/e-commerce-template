package com.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class Payment {
    private Long id;
    private Long orderId;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String paymentGateway;
    private Map<String, Object> gatewayResponse;
    private String failureReason;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For joined data
    private Order order;
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED
    }
    
    // Constructors
    public Payment() {}
    
    public Payment(Long orderId, BigDecimal amount, String paymentMethod, String paymentGateway) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = "USD";
        this.paymentMethod = paymentMethod;
        this.paymentGateway = paymentGateway;
        this.paymentStatus = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Payment(Long orderId, String paymentIntentId, BigDecimal amount, String paymentMethod, String paymentGateway) {
        this(orderId, amount, paymentMethod, paymentGateway);
        this.paymentIntentId = paymentIntentId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        this.paymentStatus = paymentStatus;
        this.updatedAt = LocalDateTime.now();
        if (paymentStatus == PaymentStatus.COMPLETED && processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }
    
    public Map<String, Object> getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(Map<String, Object> gatewayResponse) { this.gatewayResponse = gatewayResponse; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    // Utility methods
    public boolean isSuccessful() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }
    
    public boolean isFailed() {
        return paymentStatus == PaymentStatus.FAILED;
    }
    
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING || paymentStatus == PaymentStatus.PROCESSING;
    }
    
    public boolean isRefunded() {
        return paymentStatus == PaymentStatus.REFUNDED;
    }
}