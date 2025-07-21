package com.ecommerce.controller;

import com.ecommerce.config.StripeConfig;
import com.ecommerce.dto.CreatePaymentRequest;
import com.ecommerce.dto.PaymentIntentResponse;
import com.ecommerce.dto.RefundRequest;
import com.ecommerce.model.Payment;
import com.ecommerce.service.PaymentService;
import com.ecommerce.util.SecurityUtils;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;
    
    /**
     * Create a payment intent for an order
     */
    @PostMapping("/create-intent")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(
                request.getOrderId(),
                userId
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment intent: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get payment details for an order
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Payment> getPaymentByOrderId(
            @PathVariable Long orderId) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        
        // TODO: Add authorization check to ensure user has access to this order
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Process refund for a payment
     */
    @PostMapping("/refund")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<String> processRefund(
            @Valid @RequestBody RefundRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        
        try {
            paymentService.processRefund(
                request.getOrderId(),
                request.getAmount(),
                request.getReason()
            );
            return ResponseEntity.ok("Refund processed successfully");
        } catch (Exception e) {
            log.error("Error processing refund: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Stripe webhook endpoint to handle payment events
     */
    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            event = Webhook.constructEvent(
                payload,
                sigHeader,
                stripeConfig.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Stripe webhook error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }
        
        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
                
                if (paymentIntent != null) {
                    log.info("Payment succeeded for PaymentIntent: {}", paymentIntent.getId());
                    paymentService.confirmPayment(paymentIntent.getId(), "succeeded");
                }
                break;
                
            case "payment_intent.payment_failed":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
                
                if (paymentIntent != null) {
                    log.info("Payment failed for PaymentIntent: {}", paymentIntent.getId());
                    paymentService.confirmPayment(paymentIntent.getId(), "failed");
                }
                break;
                
            case "payment_intent.processing":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
                
                if (paymentIntent != null) {
                    log.info("Payment processing for PaymentIntent: {}", paymentIntent.getId());
                    paymentService.confirmPayment(paymentIntent.getId(), "processing");
                }
                break;
                
            case "payment_intent.canceled":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);
                
                if (paymentIntent != null) {
                    log.info("Payment canceled for PaymentIntent: {}", paymentIntent.getId());
                    paymentService.confirmPayment(paymentIntent.getId(), "canceled");
                }
                break;
                
            default:
                log.info("Unhandled Stripe event type: {}", event.getType());
        }
        
        return ResponseEntity.ok("Event received");
    }
}