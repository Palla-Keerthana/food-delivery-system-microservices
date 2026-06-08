package com.fooddelivery.auth.service.impl;

import com.fooddelivery.auth.client.AgentClient;
import com.fooddelivery.auth.client.CustomerClient;
import com.fooddelivery.auth.client.RestaurantClient;
import com.fooddelivery.auth.dto.*;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.exception.AuthenticationException;
import com.fooddelivery.auth.exception.InvalidRequestException;
import com.fooddelivery.auth.exception.ResourceNotFoundException;
import com.fooddelivery.auth.repository.UserRepository;
import com.fooddelivery.auth.service.AuthService;
import com.fooddelivery.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuthService} providing business logic
 * for user registration, login, and password management.
 * Handles routing of profile creation to respective microservices
 * via Feign clients based on user role.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CustomerClient customerClient;
    private final RestaurantClient restaurantClient;
    private final AgentClient agentClient;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system after validating the request.
     * Saves the user to the users table and routes profile creation
     * to the appropriate microservice based on the user's role.
     *
     * @param request the registration request containing email, password,
     *                role and profile-specific fields
     * @return success message string upon successful registration
     * @throws InvalidRequestException if the email is already registered
     */
    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}",
                    request.getEmail());
            throw new InvalidRequestException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        routeProfileCreation(request, savedUser);

        return "User registered successfully";
    }

    /**
     * Routes profile creation to the appropriate microservice
     * based on the user's role using Feign clients.
     * Failures are handled gracefully — user registration is not
     * rolled back if a downstream service is unavailable.
     *
     * @param request   the original registration request
     * @param savedUser the saved user entity containing the generated userId
     */
    private void routeProfileCreation(RegisterRequest request, User savedUser) {
        switch (request.getRole()) {
            case CUSTOMER -> createCustomerProfile(request, savedUser.getUserId());
            case RESTAURANT_OWNER -> createRestaurantProfile(request, savedUser.getUserId());
            case AGENT -> createAgentProfile(request, savedUser.getUserId());
        }
    }

    /**
     * Creates a customer profile in the customer-service via Feign client.
     *
     * @param request the registration request containing customer details
     * @param userId  the generated userId from the saved user
     */
    private void createCustomerProfile(RegisterRequest request, Long userId) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setUserId(userId);
        customerRequest.setCustomerName(request.getName());
        customerRequest.setPhone(request.getPhone());
        customerRequest.setAddress(request.getAddress());
        try {
            customerClient.createProfile(customerRequest);
            log.info("Customer profile created for userId: {}", userId);
        } catch (Exception e) {
            log.error("Customer service unavailable, profile not created " +
                    "for userId: {} - {}", userId, e.getMessage());
        }
    }

    /**
     * Creates a restaurant profile in the menu-service via Feign client.
     *
     * @param request the registration request containing restaurant details
     * @param userId  the generated userId from the saved user
     */
    private void createRestaurantProfile(RegisterRequest request, Long userId) {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setUserId(userId);
        restaurantRequest.setRestaurantName(request.getRestaurantName());
        restaurantRequest.setLocation(request.getLocation());
        restaurantRequest.setContactNumber(request.getContactNumber());
        try {
            restaurantClient.registerRestaurant(restaurantRequest);
            log.info("Restaurant profile created for userId: {}", userId);
        } catch (Exception e) {
            log.error("Restaurant service unavailable, profile not created " +
                    "for userId: {} - {}", userId, e.getMessage());
        }
    }

    /**
     * Creates an agent profile in the delivery-service via Feign client.
     *
     * @param request the registration request containing agent details
     * @param userId  the generated userId from the saved user
     */
    private void createAgentProfile(RegisterRequest request, Long userId) {
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setUserId(userId);
        agentRequest.setName(request.getName());
        agentRequest.setPhone(request.getPhone());
        log.debug("Sending agent profile to delivery-service - " +
                        "userId: {}, name: {}, phone: {}",
                userId, request.getName(), request.getPhone());
        try {
            agentClient.registerAgent(agentRequest);
            log.info("Agent profile created for userId: {}", userId);
        } catch (Exception e) {
            log.error("Agent service unavailable, profile not created " +
                    "for userId: {} - {}", userId, e.getMessage());
        }
    }

    /**
     * Authenticates a user by verifying email and BCrypt-hashed password.
     * Generates a JWT token upon successful authentication.
     *
     * @param request the login request containing email and password
     * @return {@link LoginResponse} containing userId, token, role and email
     * @throws AuthenticationException if credentials are invalid or
     *                                 account is inactive
     */
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed - no user found for email: {}",
                            request.getEmail());
                    return new AuthenticationException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - incorrect password for email: {}",
                    request.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("Login failed - inactive account for email: {}",
                    request.getEmail());
            throw new AuthenticationException("User account is inactive");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getRole().name());
        log.info("User logged in successfully: {}", user.getEmail());

        return new LoginResponse(
                user.getUserId(),
                token,
                user.getRole().name(),
                user.getEmail());
    }

    /**
     * Updates the password of an existing user after validating
     * the current password and ensuring the new password meets requirements.
     *
     * @param userId  the ID of the user whose password is to be updated
     * @param request the password update request containing current,
     *                new and confirm passwords
     * @return success message string upon successful password update
     * @throws ResourceNotFoundException if no user exists with the given ID
     * @throws AuthenticationException   if the current password is incorrect
     * @throws InvalidRequestException   if new and confirm passwords do not
     *                                   match or new password is same as current
     */
    @Override
    public String updatePassword(Long userId, UpdatePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Password update failed - user not found " +
                            "for userId: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(
                request.getCurrentPassword(), user.getPassword())) {
            log.warn("Password update failed - incorrect current password " +
                    "for userId: {}", userId);
            throw new AuthenticationException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password update failed - passwords do not match " +
                    "for userId: {}", userId);
            throw new InvalidRequestException(
                    "New password and confirm password do not match");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(), user.getPassword())) {
            log.warn("Password update failed - new password same as current " +
                    "for userId: {}", userId);
            throw new InvalidRequestException(
                    "New password cannot be same as current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password updated successfully for userId: {}", userId);

        return "Password updated successfully";
    }
}