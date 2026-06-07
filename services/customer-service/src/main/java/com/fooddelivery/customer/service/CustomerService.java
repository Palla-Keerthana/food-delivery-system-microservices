package com.fooddelivery.customer.service;

import com.fooddelivery.customer.dto.CustomerRequest;
import com.fooddelivery.customer.dto.CustomerResponse;

/**
 * Service interface for customer profile operations.
 * Defines the business logic contracts for creating,
 * retrieving and updating customer profiles.
 * Implemented by
 * {@link com.fooddelivery.customer.service.impl.CustomerServiceImpl}.
 */
public interface CustomerService {

    /**
     * Creates a new customer profile in customer_db.
     * Called automatically by auth-service via Feign during
     * user registration when role is CUSTOMER.
     *
     * @param request the customer request containing userId,
     *                customerName, phone and address
     * @return {@link CustomerResponse} with the created customer details
     * @throws com.fooddelivery.customer.exception.InvalidRequestException
     *         if a customer profile already exists for the given userId
     */
    CustomerResponse createCustomer(CustomerRequest request);

    /**
     * Retrieves a customer profile by their customer ID.
     * Used when the customerId is known directly.
     *
     * @param customerId the unique identifier of the customer
     * @return {@link CustomerResponse} with the found customer details
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer exists with the given customerId
     */
    CustomerResponse getCustomerById(Long customerId);

    /**
     * Retrieves a customer profile by their user ID.
     * Used after login when only the userId from JWT token is available.
     *
     * @param userId the user ID linked to the customer profile
     * @return {@link CustomerResponse} with the found customer details
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer profile exists for the given userId
     */
    CustomerResponse getCustomerByUserId(Long userId);

    /**
     * Updates an existing customer profile with new details.
     * Only customerName, phone and address can be updated.
     * userId cannot be changed.
     *
     * @param customerId the unique identifier of the customer to update
     * @param request    the customer request containing updated
     *                   customerName, phone and address
     * @return {@link CustomerResponse} with the updated customer details
     * @throws com.fooddelivery.customer.exception.ResourceNotFoundException
     *         if no customer exists with the given customerId
     */
    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}