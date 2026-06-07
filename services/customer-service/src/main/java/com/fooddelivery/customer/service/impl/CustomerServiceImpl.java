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

/**
 * Implementation of {@link CustomerService} providing business logic
 * for customer profile management.
 * Handles creation, retrieval and update of customer profiles
 * stored in the {@code customers} table in customer_db.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Creates a new customer profile in customer_db.
     * Validates that no existing profile exists for the given userId
     * before saving the new customer record.
     *
     * @param request the customer request containing userId,
     *                customerName, phone and address
     * @return {@link CustomerResponse} with the created customer details
     * @throws InvalidRequestException if a customer profile already
     *                                 exists for the given userId
     */
    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByUserId(request.getUserId())) {
            log.warn("Customer already exists for userId: {}",
                    request.getUserId());
            throw new InvalidRequestException(
                    "Customer profile already exists");
        }

        Customer customer = new Customer();
        customer.setUserId(request.getUserId());
        customer.setCustomerName(request.getCustomerName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully for userId: {}",
                saved.getUserId());

        return mapToResponse(saved);
    }

    /**
     * Retrieves a customer profile by their customer ID.
     * Used when the customerId is known directly.
     *
     * @param customerId the unique identifier of the customer
     * @return {@link CustomerResponse} with the found customer details
     * @throws ResourceNotFoundException if no customer exists
     *                                   with the given customerId
     */
    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        log.debug("Fetching customer by customerId: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for customerId: {}",
                            customerId);
                    return new ResourceNotFoundException(
                            "Customer not found");
                });
        return mapToResponse(customer);
    }

    /**
     * Retrieves a customer profile by their user ID.
     * Used after login when only the userId from JWT token is available.
     *
     * @param userId the user ID linked to the customer profile
     * @return {@link CustomerResponse} with the found customer details
     * @throws ResourceNotFoundException if no customer profile exists
     *                                   for the given userId
     */
    @Override
    public CustomerResponse getCustomerByUserId(Long userId) {
        log.debug("Fetching customer by userId: {}", userId);
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for userId: {}", userId);
                    return new ResourceNotFoundException(
                            "Customer not found for user");
                });
        return mapToResponse(customer);
    }

    /**
     * Updates an existing customer profile with new details.
     * Only customerName, phone and address are updated.
     * userId remains unchanged to maintain the link to auth_db.
     *
     * @param customerId the unique identifier of the customer to update
     * @param request    the customer request containing updated
     *                   customerName, phone and address
     * @return {@link CustomerResponse} with the updated customer details
     * @throws ResourceNotFoundException if no customer exists
     *                                   with the given customerId
     */
    @Override
    public CustomerResponse updateCustomer(Long customerId,
                                           CustomerRequest request) {
        log.debug("Updating customer for customerId: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for update, " +
                            "customerId: {}", customerId);
                    return new ResourceNotFoundException(
                            "Customer not found");
                });

        customer.setCustomerName(request.getCustomerName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated successfully for customerId: {}",
                updated.getCustomerId());

        return mapToResponse(updated);
    }

    /**
     * Converts a {@link Customer} entity to a {@link CustomerResponse} DTO.
     * Ensures the entity is never exposed directly outside the service layer.
     *
     * @param customer the customer entity to convert
     * @return {@link CustomerResponse} containing the customer details
     */
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