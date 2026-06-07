package com.fooddelivery.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for user login requests.
 * Contains credentials required to authenticate a user
 * and generate a JWT token.
 */
@Data
public class LoginRequest {

    /**
     * Email address of the user.
     * Must be a valid email format and cannot be blank.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Plain text password of the user.
     * Verified against the BCrypt hashed password stored in auth_db.
     * Cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;
}