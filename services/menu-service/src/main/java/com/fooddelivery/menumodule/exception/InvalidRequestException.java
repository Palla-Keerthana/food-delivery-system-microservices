package com.fooddelivery.menumodule.exception;

/**
 * Thrown when user provides invalid input.
 * Example: empty name, zero price, invalid ID.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}