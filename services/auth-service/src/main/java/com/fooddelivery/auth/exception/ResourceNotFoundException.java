package com.fooddelivery.auth.exception;

/**
 * Exception thrown when a requested resource
 * does not exist in the database.
 * Triggered in the following scenarios:
 * <ul>
 *   <li>User not found by ID during password update</li>
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