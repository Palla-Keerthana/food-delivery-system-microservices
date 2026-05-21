package com.fooddelivery.menumodule.exception;

/**
 * Thrown when a restaurant is not found in the database.
 * Example: Restaurant not found with ID: 5
 */
public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}