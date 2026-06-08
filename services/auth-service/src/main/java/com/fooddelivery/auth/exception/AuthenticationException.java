package com.fooddelivery.auth.exception;

/**
 * Exception thrown when authentication fails.
 * Triggered in the following scenarios:
 * <ul>
 *   <li>Invalid email or password during login</li>
 *   <li>User account is inactive</li>
 *   <li>Current password is incorrect during password update</li>
 * </ul>
 * Results in {@code 401 Unauthorized} HTTP response.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructs a new AuthenticationException with the given message.
     *
     * @param message the detail message describing the authentication failure
     */
    public AuthenticationException(String message) {
        super(message);
    }
}