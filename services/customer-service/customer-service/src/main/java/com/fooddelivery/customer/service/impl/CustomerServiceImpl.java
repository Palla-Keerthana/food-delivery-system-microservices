package com.fooddelivery.customer.service.impl;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;
import com.fooddelivery.customer.entity.Customer;
import com.fooddelivery.customer.exception.InvalidRequestException;
import com.fooddelivery.customer.exception.ResourceNotFoundException;
import com.fooddelivery.customer.repository.CustomerRepository;
import com.fooddelivery.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        // Check if customer already exists for this userId
        if (customerRepository.existsByUserId(request.getUserId())) {
            log.warn("Customer already exists for userId: {}", request.getUserId());
            throw new InvalidRequestException("Customer profile already exists");
        }

        // Build and save customer
        Customer customer = new Customer();
        customer.setUserId(request.getUserId());
        customer.setCustomerName(request.getCustomerName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully for userId: {}", saved.getUserId());

        return mapToResponse(saved);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        log.debug("Fetching customer by ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for ID: {}", customerId);
                    return new ResourceNotFoundException("Customer not found");
                });
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByUserId(Long userId) {
        log.debug("Fetching customer by userId: {}", userId);
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for userId: {}", userId);
                    return new ResourceNotFoundException("Customer not found for user");
                });
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        log.debug("Updating customer ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for update, ID: {}", customerId);
                    return new ResourceNotFoundException("Customer not found");
                });

        customer.setCustomerName(request.getCustomerName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated successfully, ID: {}", updated.getCustomerId());

        return mapToResponse(updated);
    }

    // Convert Entity to Response DTO
    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getUserId(),
                customer.getCustomerName(),
                customer.getPhone(),
                customer.getAddress()
        );
    }
}