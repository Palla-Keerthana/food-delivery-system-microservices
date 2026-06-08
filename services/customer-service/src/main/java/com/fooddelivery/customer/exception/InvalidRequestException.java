package com.fooddelivery.customer.exception;

/**
 * Exception thrown when a request contains invalid data
 * or violates business rules in the customer-service.
 * Triggered in the following scenarios:
 * <ul>
 *   <li>Customer profile already exists for the given userId</li>
 * </ul>
 * Results in {@code 400 Bad Request} HTTP response.
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Constructs a new InvalidRequestException with the given message.
     *
     * @param message the detail message describing the invalid request
     */
    public InvalidRequestException(String message) {
        super(message);
    }
}