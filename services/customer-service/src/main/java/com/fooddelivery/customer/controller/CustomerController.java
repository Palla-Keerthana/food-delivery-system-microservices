package com.fooddelivery.customer.controller;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;
import com.fooddelivery.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing customer profiles.
 * Provides endpoints for creating, retrieving and updating
 * customer profiles stored in customer_db.
 * All endpoints require a valid JWT token passed via
 * the Authorization header, validated at the API Gateway.
 * Base URL: {@code /api/customers}
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Creates a new customer profile in the system.
     * Usually called automatically by auth-service via Feign
     * during user registration when role is CUSTOMER.
     *
     * @param request the customer request containing userId,
     *                customerName, phone and address
     * @return {@code 201 Created} with the created {@link CustomerResponse}
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        log.info("Create customer request received for userId: {}",
                request.getUserId());
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a customer profile by their customer ID.
     * Used when the customerId is known directly.
     *
     * @param customerId the unique identifier of the customer
     * @return {@code 200 OK} with the found {@link CustomerResponse}
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer exists with the given customerId
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Long customerId) {
        log.info("Get customer request received for customerId: {}",
                customerId);
        CustomerResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a customer profile by their user ID.
     * Used after login when only the userId from JWT token is available.
     *
     * @param userId the user ID linked to the customer profile
     * @return {@code 200 OK} with the found {@link CustomerResponse}
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer exists linked to the given userId
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomerResponse> getCustomerByUserId(
            @PathVariable Long userId) {
        log.info("Get customer request received for userId: {}", userId);
        CustomerResponse response = customerService.getCustomerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing customer profile with new details.
     * Only customerName, phone and address can be updated.
     * userId cannot be changed.
     *
     * @param customerId the unique identifier of the customer to update
     * @param request    the customer request containing updated
     *                   customerName, phone and address
     * @return {@code 200 OK} with the updated {@link CustomerResponse}
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer exists with the given customerId
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequest request) {
        log.info("Update customer request received for customerId: {}",
                customerId);
        CustomerResponse response = customerService.updateCustomer(
                customerId, request);
        return ResponseEntity.ok(response);
    }
}