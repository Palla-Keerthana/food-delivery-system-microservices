package com.fooddelivery.menumodule.exception;

/**
 * Thrown when a requested resource is not found in the system.
 * Example: Restaurant not found, User not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}