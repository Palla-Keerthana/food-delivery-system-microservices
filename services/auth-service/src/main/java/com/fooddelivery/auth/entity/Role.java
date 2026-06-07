package com.fooddelivery.auth.entity;

/**
 * Enum representing the roles available in the food delivery system.
 * Each role determines the type of user and their access permissions.
 */
public enum Role {

    /** Regular customer who can place orders */
    CUSTOMER,

    /** Restaurant owner who can manage restaurant and menu */
    RESTAURANT_OWNER,

    /** Delivery agent who can handle deliveries */
    AGENT
}