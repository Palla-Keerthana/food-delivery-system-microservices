package com.fooddelivery.menumodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity                            // ← tells JPA this is a DB table
@Table(name = "restaurants")       // ← maps to restaurants table in DB
public class Restaurant {

    @Id                            // ← primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ← auto increment
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "user_id")      // ← foreign key to users table
    private Long userId;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "location")
    private String location;

    @Column(name = "contact_number")
    private String contactNumber;
}