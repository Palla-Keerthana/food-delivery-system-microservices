package com.fooddelivery.auth.service;

import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;
import com.fooddelivery.auth.dto.UpdatePasswordRequest;

/**
 * Service interface for authentication operations.
 * Defines the business logic contracts for user registration,
 * login and password management.
 * Implemented by {@link com.fooddelivery.auth.service.impl.AuthServiceImpl}.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     * Saves user credentials to auth_db and routes profile
     * creation to the appropriate microservice based on role.
     *
     * @param request the registration request containing email,
     *                password, role and profile-specific fields
     * @return success message string upon successful registration
     * @throws com.fooddelivery.auth.exception.InvalidRequestException
     *         if the email is already registered
     */
    String register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     * Verifies credentials and generates a JWT token
     * upon successful authentication.
     *
     * @param request the login request containing email and password
     * @return {@link LoginResponse} containing userId, token, role and email
     * @throws com.fooddelivery.auth.exception.AuthenticationException
     *         if credentials are invalid or account is inactive
     */
    LoginResponse login(LoginRequest request);

    /**
     * Updates the password of an existing user.
     * Validates current password before allowing the update.
     * Ensures new password is different from the current one.
     *
     * @param userId  the ID of the user whose password is to be updated
     * @param request the password update request containing currentPassword,
     *                newPassword and confirmPassword
     * @return success message string upon successful password update
     * @throws com.fooddelivery.auth.exception.ResourceNotFoundException
     *         if no user exists with the given userId
     * @throws com.fooddelivery.auth.exception.AuthenticationException
     *         if the current password is incorrect
     * @throws com.fooddelivery.auth.exception.InvalidRequestException
     *         if new and confirm passwords do not match or
     *         new password is same as current password
     */
    String updatePassword(Long userId, UpdatePasswordRequest request);
}