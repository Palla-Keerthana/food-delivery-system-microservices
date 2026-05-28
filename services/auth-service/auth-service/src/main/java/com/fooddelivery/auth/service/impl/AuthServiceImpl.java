package com.fooddelivery.auth.service.impl;

import com.fooddelivery.auth.client.AgentClient;
import com.fooddelivery.auth.client.CustomerClient;
import com.fooddelivery.auth.client.RestaurantClient;
import com.fooddelivery.auth.dto.*;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.exception.AuthenticationException;
import com.fooddelivery.auth.exception.InvalidRequestException;
import com.fooddelivery.auth.repository.UserRepository;
import com.fooddelivery.auth.service.AuthService;
import com.fooddelivery.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public String register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new InvalidRequestException("Email already registered");
        }

        // Build and save user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        // Route to correct service based on role
        switch (request.getRole()) {
            case CUSTOMER -> {
                CustomerRequest customerRequest = new CustomerRequest();
                customerRequest.setUserId(savedUser.getUserId());
                customerRequest.setCustomerName(request.getName());
                customerRequest.setPhone(request.getPhone());
                customerRequest.setAddress(request.getAddress());
                try {
                    customerClient.createProfile(customerRequest);
                    log.info("Customer profile created for userId: {}", savedUser.getUserId());
                } catch (Exception e) {
                    log.error("Customer service unavailable, profile not created: {}", e.getMessage());
                }
            }
            case RESTAURANT_OWNER -> {
                RestaurantRequest restaurantRequest = new RestaurantRequest();
                restaurantRequest.setUserId(savedUser.getUserId());
                restaurantRequest.setRestaurantName(request.getRestaurantName());
                restaurantRequest.setLocation(request.getLocation());
                restaurantRequest.setContactNumber(request.getContactNumber());
                try {
                    restaurantClient.registerRestaurant(restaurantRequest); // ← was createProfile
                    log.info("Restaurant profile created for userId: {}", savedUser.getUserId());
                } catch (Exception e) {
                    log.error("Restaurant service unavailable, profile not created: {}", e.getMessage());
                }
            }
            case AGENT -> {
                AgentRequest agentRequest = new AgentRequest();
                agentRequest.setUserId(savedUser.getUserId());
                agentRequest.setName(request.getName());     // ← was setAgentName
                agentRequest.setPhone(request.getPhone());   // ← was setContactNumber
                try {
                    agentClient.registerAgent(agentRequest); // ← was createProfile
                    log.info("Agent profile created for userId: {}", savedUser.getUserId());
                } catch (Exception e) {
                    log.error("Agent service unavailable, profile not created: {}", e.getMessage());
                }
            }
        }

        return "User registered successfully";
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed - no user found: {}", request.getEmail());
                    return new AuthenticationException("Invalid credentials");
                });

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - incorrect password: {}", request.getEmail());
            throw new AuthenticationException("Invalid credentials");
        }

        // Check status
        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("Login failed - inactive account: {}", request.getEmail());
            throw new AuthenticationException("User account is inactive");
        }

        // Generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("User logged in successfully: {}", user.getEmail());

        return new LoginResponse(token, user.getRole().name(), user.getEmail());
    }
}