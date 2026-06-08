package com.fooddelivery.auth.controller;

import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;
import com.fooddelivery.auth.dto.UpdatePasswordRequest;
import com.fooddelivery.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling authentication operations.
 * Provides endpoints for user registration, login and password management.
 * All endpoints are publicly accessible as JWT validation is
 * handled at the API Gateway level.
 * Base URL: {@code /auth}
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user in the system.
     * Creates a user record in auth_db and routes profile creation
     * to the appropriate microservice based on the user's role.
     *
     * @param request the registration request containing email, password,
     *                role and profile-specific fields
     * @return {@code 200 OK} with success message
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     * Token contains userId, email and role for use in subsequent requests.
     *
     * @param request the login request containing email and password
     * @return {@code 200 OK} with {@link LoginResponse} containing
     *         userId, token, role and email
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the password of an existing user.
     * Validates current password before allowing update.
     * New password must be different from the current password.
     *
     * @param userId  the ID of the user whose password is to be updated
     * @param request the password update request containing currentPassword,
     *                newPassword and confirmPassword
     * @return {@code 200 OK} with success message
     */
    @PutMapping("/update-password/{userId}")
    public ResponseEntity<String> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordRequest request) {
        log.info("Password update request received for userId: {}", userId);
        String response = authService.updatePassword(userId, request);
        return ResponseEntity.ok(response);
    }
}