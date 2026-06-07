package com.fooddelivery.auth.dto;

import lombok.Data;

/**
 * Data Transfer Object for creating a restaurant profile
 * in the menu-service via Feign client.
 * Populated from {@link RegisterRequest} during user registration
 * when role is {@code RESTAURANT_OWNER}.
 */
@Data
public class RestaurantRequest {

    /**
     * The unique identifier of the registered user.
     * Links the restaurant profile to the user in auth_db.
     */
    private Long userId;

    /**
     * Name of the restaurant.
     */
    private String restaurantName;

    /**
     * Physical location or address of the restaurant.
     */
    private String location;

    /**
     * Contact phone number of the restaurant.
     */
    private String contactNumber;
}