package com.ecommerce.service;

import com.ecommerce.dto.CreateAddressRequest;
import com.ecommerce.dto.UpdateAddressRequest;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    public Address createAddress(Long userId, CreateAddressRequest request) {
        // If this is the user's first address or is set as default, unset other defaults
        if (request.getIsDefault()) {
            addressMapper.clearDefaultForUser(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        address.setType(request.getType() != null ? request.getType() : Address.AddressType.HOME);
        address.setFirstName(request.getFirstName());
        address.setLastName(request.getLastName());
        address.setPhone(request.getPhone());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        address.setIsDefault(request.getIsDefault());

        addressMapper.insert(address);
        
        // If this is the user's first address, make it default
        List<Address> userAddresses = addressMapper.findByUserId(userId);
        if (userAddresses.size() == 1 && !address.getIsDefault()) {
            address.setIsDefault(true);
            addressMapper.update(address);
        }

        return address;
    }

    public Address updateAddress(Long userId, Long addressId, UpdateAddressRequest request) {
        Address address = addressMapper.findById(addressId).orElseThrow(
            () -> new RuntimeException("Address not found")
        );

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Address does not belong to user");
        }

        // Update fields if provided
        if (request.getType() != null) {
            address.setType(request.getType());
        }
        if (request.getFirstName() != null) {
            address.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            address.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            address.setPhone(request.getPhone());
        }
        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getState() != null) {
            address.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
            // If setting as default, unset other defaults
            addressMapper.clearDefaultForUser(userId);
            address.setIsDefault(true);
        }

        addressMapper.update(address);
        return address;
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.findById(addressId).orElseThrow(
            () -> new RuntimeException("Address not found")
        );

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Address does not belong to user");
        }

        boolean wasDefault = address.getIsDefault();
        addressMapper.deleteById(addressId);

        // If the deleted address was default, set another address as default
        if (wasDefault) {
            List<Address> remainingAddresses = addressMapper.findByUserId(userId);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressMapper.update(newDefault);
            }
        }
    }

    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.findById(addressId).orElseThrow(
            () -> new RuntimeException("Address not found")
        );

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("Address does not belong to user");
        }

        if (!address.getIsDefault()) {
            // Unset other defaults and set this as default
            addressMapper.clearDefaultForUser(userId);
            address.setIsDefault(true);
            addressMapper.update(address);
        }

        return address;
    }

    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId).orElse(null);
    }
}