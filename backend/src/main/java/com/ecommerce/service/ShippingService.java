package com.ecommerce.service;

import com.ecommerce.dto.ShippingMethodResponse;
import com.ecommerce.mapper.ShippingMethodMapper;
import com.ecommerce.model.ShippingMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShippingService {
    
    private final ShippingMethodMapper shippingMethodMapper;
    
    public ShippingService(ShippingMethodMapper shippingMethodMapper) {
        this.shippingMethodMapper = shippingMethodMapper;
    }
    
    public List<ShippingMethodResponse> getAvailableShippingMethods() {
        List<ShippingMethod> methods = shippingMethodMapper.findActiveShippingMethods();
        return methods.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public ShippingMethodResponse getShippingMethodById(Long methodId) {
        Optional<ShippingMethod> methodOpt = shippingMethodMapper.findById(methodId);
        if (methodOpt.isEmpty()) {
            throw new RuntimeException("Shipping method not found");
        }
        return convertToResponse(methodOpt.get());
    }
    
    public ShippingMethodResponse calculateShippingCost(Long methodId, String zipCode, Double weight) {
        Optional<ShippingMethod> methodOpt = shippingMethodMapper.findById(methodId);
        if (methodOpt.isEmpty()) {
            throw new RuntimeException("Shipping method not found");
        }
        
        ShippingMethod method = methodOpt.get();
        ShippingMethodResponse response = convertToResponse(method);
        
        // TODO: Implement more sophisticated shipping calculation based on zip code and weight
        // For now, return the base price
        
        return response;
    }
    
    private ShippingMethodResponse convertToResponse(ShippingMethod method) {
        return new ShippingMethodResponse(
            method.getId(),
            method.getName(),
            method.getDescription(),
            method.getPrice(),
            method.getEstimatedDaysMin(),
            method.getEstimatedDaysMax()
        );
    }
}