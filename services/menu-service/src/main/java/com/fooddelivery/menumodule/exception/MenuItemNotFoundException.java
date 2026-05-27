package com.fooddelivery.menumodule.exception;

/**
 * Thrown when a menu item is not found in the database.
 * Example: Menu item not found with ID: 99
 */
public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}