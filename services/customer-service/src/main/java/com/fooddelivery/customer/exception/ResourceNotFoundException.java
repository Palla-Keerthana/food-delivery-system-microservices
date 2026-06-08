package com.fooddelivery.customer.exception;

/**
 * Exception thrown when a requested customer resource
 * does not exist in the database.
 * Triggered in the following scenarios:
 * <ul>
 *   <li>Customer not found by customerId</li>
 *   <li>Customer not found by userId</li>
 *   <li>Customer not found during profile update</li>
 * </ul>
 * Results in {@code 404 Not Found} HTTP response.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the given message.
     *
     * @param message the detail message describing the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}