package com.ecommerce.mapper;

import com.ecommerce.model.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {
    
    // Payment CRUD operations
    void insertPayment(Payment payment);
    void updatePayment(Payment payment);
    void deletePaymentById(@Param("id") Long id);
    Optional<Payment> findPaymentById(@Param("id") Long id);
    Optional<Payment> findPaymentByTransactionId(@Param("transactionId") String transactionId);
    Optional<Payment> findPaymentByPaymentIntentId(@Param("paymentIntentId") String paymentIntentId);
    
    // Order payments
    List<Payment> findPaymentsByOrderId(@Param("orderId") Long orderId);
    Optional<Payment> findLatestPaymentByOrderId(@Param("orderId") Long orderId);
    Optional<Payment> findSuccessfulPaymentByOrderId(@Param("orderId") Long orderId);
    
    // Payment status management
    void updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Payment.PaymentStatus paymentStatus);
    void updatePaymentTransaction(@Param("id") Long id, 
                                 @Param("transactionId") String transactionId,
                                 @Param("paymentStatus") Payment.PaymentStatus paymentStatus);
    void markPaymentAsProcessed(@Param("id") Long id, 
                               @Param("transactionId") String transactionId,
                               @Param("processedAt") LocalDateTime processedAt);
    void markPaymentAsFailed(@Param("id") Long id, @Param("failureReason") String failureReason);
    
    // Payment queries by status
    List<Payment> findPaymentsByStatus(@Param("paymentStatus") Payment.PaymentStatus paymentStatus);
    List<Payment> findPendingPayments();
    List<Payment> findFailedPayments();
    List<Payment> findSuccessfulPayments();
    
    // Payment statistics
    long countPaymentsByStatus(@Param("paymentStatus") Payment.PaymentStatus paymentStatus);
    long countPaymentsByGateway(@Param("paymentGateway") String paymentGateway);
    java.math.BigDecimal getTotalPaymentAmount();
    java.math.BigDecimal getTotalPaymentAmountByStatus(@Param("paymentStatus") Payment.PaymentStatus paymentStatus);
    java.math.BigDecimal getTotalPaymentAmountByGateway(@Param("paymentGateway") String paymentGateway);
    
    // Payment analytics
    List<Payment> findPaymentsCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    List<Payment> findRecentPayments(@Param("limit") int limit);
    
    // Gateway specific queries
    List<Payment> findPaymentsByGateway(@Param("paymentGateway") String paymentGateway);
    List<Payment> findPaymentsByMethod(@Param("paymentMethod") String paymentMethod);
    
    // Check existence
    boolean existsPaymentById(@Param("id") Long id);
    boolean existsPaymentByTransactionId(@Param("transactionId") String transactionId);
    boolean existsPaymentByPaymentIntentId(@Param("paymentIntentId") String paymentIntentId);
}