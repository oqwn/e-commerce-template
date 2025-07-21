package com.ecommerce.service;

import com.ecommerce.config.StripeConfig;
import com.ecommerce.dto.CreatePaymentRequest;
import com.ecommerce.dto.PaymentIntentResponse;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.PaymentMapper;
import com.ecommerce.model.Order;
import com.ecommerce.model.Payment;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final StripeConfig stripeConfig;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    
    /**
     * Create a payment intent for an order
     */
    @Transactional
    public PaymentIntentResponse createPaymentIntent(Long orderId, Long userId) {
        // Get order details
        Order order = orderMapper.findOrderById(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        // Verify order belongs to user
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        // Check if payment already exists
        Payment existingPayment = paymentMapper.findLatestPaymentByOrderId(orderId).orElse(null);
        if (existingPayment != null && Payment.PaymentStatus.COMPLETED.equals(existingPayment.getPaymentStatus())) {
            throw new RuntimeException("Payment already completed for this order");
        }
        
        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            Long amountInCents = order.getTotalAmount()
                .multiply(new BigDecimal(100))
                .longValue();
            
            // Create metadata for the payment
            Map<String, String> metadata = new HashMap<>();
            metadata.put("orderId", orderId.toString());
            metadata.put("userId", userId.toString());
            metadata.put("orderNumber", order.getOrderNumber());
            
            // Create Stripe payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(stripeConfig.getCurrency().toLowerCase())
                .setDescription("Order #" + order.getOrderNumber())
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            // Create or update payment record
            Payment payment;
            if (existingPayment != null) {
                payment = existingPayment;
                payment.setTransactionId(paymentIntent.getId());
                payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentMapper.updatePayment(payment);
            } else {
                payment = new Payment();
                payment.setOrderId(orderId);
                payment.setAmount(order.getTotalAmount());
                payment.setCurrency(stripeConfig.getCurrency());
                payment.setPaymentMethod("CARD");
                payment.setPaymentGateway("STRIPE");
                payment.setTransactionId(paymentIntent.getId());
                payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                paymentMapper.insertPayment(payment);
            }
            
            // Return response
            PaymentIntentResponse response = new PaymentIntentResponse();
            response.setClientSecret(paymentIntent.getClientSecret());
            response.setPaymentIntentId(paymentIntent.getId());
            response.setAmount(order.getTotalAmount());
            response.setCurrency(stripeConfig.getCurrency());
            response.setStatus(paymentIntent.getStatus());
            
            return response;
            
        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: ", e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }
    
    /**
     * Confirm payment completion (called from webhook)
     */
    @Transactional
    public void confirmPayment(String paymentIntentId, String status) {
        Payment payment = paymentMapper.findPaymentByTransactionId(paymentIntentId).orElse(null);
        if (payment == null) {
            log.warn("Payment not found for transaction ID: {}", paymentIntentId);
            return;
        }
        
        // Update payment status
        Payment.PaymentStatus newStatus = mapStripeStatusToPaymentStatus(status);
        payment.setPaymentStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentMapper.updatePayment(payment);
        
        // Update order payment status if payment succeeded
        if (Payment.PaymentStatus.COMPLETED.equals(newStatus)) {
            Order order = orderMapper.findOrderById(payment.getOrderId()).orElse(null);
            if (order != null) {
                order.setPaymentStatus(Order.PaymentStatus.COMPLETED);
                order.setStatus(Order.OrderStatus.CONFIRMED);
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateOrder(order);
            }
        }
    }
    
    /**
     * Process refund for a payment
     */
    @Transactional
    public void processRefund(Long orderId, BigDecimal amount, String reason) {
        Payment payment = paymentMapper.findSuccessfulPaymentByOrderId(orderId).orElse(null);
        if (payment == null || !Payment.PaymentStatus.COMPLETED.equals(payment.getPaymentStatus())) {
            throw new RuntimeException("No completed payment found for order");
        }
        
        try {
            // Create refund in Stripe
            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getTransactionId())
                .setAmount(amount.multiply(new BigDecimal(100)).longValue())
                .setReason(mapRefundReason(reason))
                .build();
            
            Refund refund = Refund.create(params);
            
            // Update payment status
            if (amount.compareTo(payment.getAmount()) >= 0) {
                payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            } else {
                // Note: PARTIALLY_REFUNDED status would need to be added to enum if needed
                payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            }
            payment.setRefundAmount(amount);
            payment.setRefundReason(reason);
            payment.setRefundDate(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentMapper.updatePayment(payment);
            
            // Update order status
            Order order = orderMapper.findOrderById(orderId).orElse(null);
            if (order != null) {
                order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
                order.setStatus(Order.OrderStatus.REFUNDED);
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateOrder(order);
            }
            
            log.info("Refund processed successfully for order {} - Refund ID: {}", orderId, refund.getId());
            
        } catch (StripeException e) {
            log.error("Stripe error processing refund: ", e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }
    
    /**
     * Get payment details by order ID
     */
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentMapper.findLatestPaymentByOrderId(orderId).orElse(null);
    }
    
    /**
     * Map Stripe payment intent status to our payment status
     */
    private Payment.PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus.toLowerCase()) {
            case "succeeded" -> Payment.PaymentStatus.COMPLETED;
            case "processing" -> Payment.PaymentStatus.PROCESSING;
            case "requires_payment_method", "requires_confirmation", "requires_action" -> Payment.PaymentStatus.PENDING;
            case "canceled" -> Payment.PaymentStatus.CANCELLED;
            default -> Payment.PaymentStatus.FAILED;
        };
    }
    
    /**
     * Map refund reason to Stripe refund reason
     */
    private RefundCreateParams.Reason mapRefundReason(String reason) {
        return switch (reason.toUpperCase()) {
            case "DUPLICATE" -> RefundCreateParams.Reason.DUPLICATE;
            case "FRAUDULENT" -> RefundCreateParams.Reason.FRAUDULENT;
            default -> RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        };
    }
}