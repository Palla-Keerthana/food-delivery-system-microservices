package com.fooddelivery.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for customer profile creation
 * and update requests.
 * Used by {@code CustomerController} for both
 * POST and PUT endpoints.
 */
@Data
public class CustomerRequest {

    /**
     * The unique identifier of the registered user.
     * Links the customer profile to the user in auth_db.
     * Cannot be null.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Full name of the customer.
     * Cannot be blank.
     */
    @NotBlank(message = "Customer name is required")
    private String customerName;

    /**
     * Contact phone number of the customer.
     * Optional field.
     */
    private String phone;

    /**
     * Delivery address of the customer.
     * Optional field.
     */
    private String address;
}