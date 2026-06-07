package com.fooddelivery.auth.exception;

/**
 * Exception thrown when a request contains invalid data
 * or violates business rules.
 * Triggered in the following scenarios:
 * <ul>
 *   <li>Email already registered during user registration</li>
 *   <li>New password and confirm password do not match</li>
 *   <li>New password is same as current password</li>
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