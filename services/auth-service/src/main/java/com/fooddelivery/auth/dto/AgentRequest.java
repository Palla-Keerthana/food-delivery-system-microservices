package com.fooddelivery.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object for creating an agent profile
 * in the delivery-service via Feign client.
 * Populated from {@link RegisterRequest} during user registration
 * when role is {@code AGENT}.
 */
@Data
public class AgentRequest {

    /**
     * The unique identifier of the registered user.
     * Links the agent profile to the user in auth_db.
     */
    private Long userId;

    /**
     * Full name of the delivery agent.
     */
    private String name;

    /**
     * Contact phone number of the delivery agent.
     */
    private String phone;
}