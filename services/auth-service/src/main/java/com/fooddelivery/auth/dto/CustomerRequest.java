package com.fooddelivery.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object for creating a customer profile
 * in the customer-service via Feign client.
 * Populated from {@link RegisterRequest} during user registration
 * when role is {@code CUSTOMER}.
 */
@Data
public class CustomerRequest {

    /**
     * The unique identifier of the registered user.
     * Links the customer profile to the user in auth_db.
     */
    private Long userId;

    /**
     * Full name of the customer.
     */
    private String customerName;

    /**
     * Contact phone number of the customer.
     */
    private String phone;

    /**
     * Delivery address of the customer.
     */
    private String address;
}