package com.fooddelivery.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for login response.
 * Returned to the client upon successful authentication.
 * Contains the JWT token and user details required
 * for subsequent authenticated requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * Unique identifier of the authenticated user.
     * Used to fetch user-specific profiles from other services.
     */
    private Long userId;

    /**
     * JWT token generated upon successful authentication.
     * Must be included in the Authorization header as
     * {@code Bearer <token>} for all protected endpoints.
     */
    private String token;

    /**
     * Role of the authenticated user.
     * One of: CUSTOMER, RESTAURANT_OWNER, AGENT.
     * Determines access permissions across services.
     */
    private String role;

    /**
     * Email address of the authenticated user.
     */
    private String email;
}