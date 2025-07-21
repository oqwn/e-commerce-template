package com.ecommerce.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StripeConfig {
    
    @Value("${app.stripe.api-key}")
    private String apiKey;
    
    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;
    
    @Value("${app.stripe.currency}")
    private String currency;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}