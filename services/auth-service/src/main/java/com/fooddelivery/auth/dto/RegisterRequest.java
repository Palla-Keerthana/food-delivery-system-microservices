package com.fooddelivery.auth.dto;

import com.fooddelivery.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for user registration requests.
 * Contains common fields required for all roles as well as
 * role-specific fields for CUSTOMER, RESTAURANT_OWNER and AGENT.
 * Role-specific fields are optional and used only when relevant.
 */
@Data
public class RegisterRequest {

    /**
     * Email address of the user.
     * Must be a valid email format, unique across all users
     * and cannot be blank.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Plain text password of the user.
     * Encoded with BCrypt before storing in auth_db.
     * Cannot be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Role of the user in the system.
     * Determines the type of profile created after registration.
     * One of: CUSTOMER, RESTAURANT_OWNER, AGENT.
     * Cannot be null.
     */
    @NotNull(message = "Role is required")
    private Role role;

    /**
     * Full name of the user.
     * Used as customerName for CUSTOMER,
     * agentName for AGENT and owner name for RESTAURANT_OWNER.
     * Cannot be blank.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * Contact phone number of the user.
     * Used for CUSTOMER and AGENT profiles.
     */
    private String phone;

    /**
     * Delivery address of the user.
     * Used only for CUSTOMER profile creation.
     */
    private String address;

    /**
     * Name of the restaurant.
     * Used only when role is RESTAURANT_OWNER.
     */
    private String restaurantName;

    /**
     * Location of the restaurant.
     * Used only when role is RESTAURANT_OWNER.
     */
    private String location;

    /**
     * Contact number of the restaurant.
     * Used only when role is RESTAURANT_OWNER.
     */
    private String contactNumber;

    /**
     * Vehicle type of the delivery agent.
     * Used only when role is AGENT.
     * Example: BIKE, CAR, SCOOTER.
     */
    private String vehicleType;
}