package com.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private String status;
}