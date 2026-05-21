package com.fooddelivery.menumodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity                          // ← tells JPA this is a DB table
@Table(name = "menu_items")      // ← maps to menu_items table in DB
public class MenuItem {

    @Id                          // ← this is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ← auto increment
    @Column(name = "item_id")    // ← maps to item_id column
    private Long itemId;

    @Column(name = "item_name", nullable = false) // ← maps to item_name column, cannot be null
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false) // ← cannot be null
    private BigDecimal price;

    @Column(name = "restaurant_id") // ← foreign key column
    private Long restaurantId;

    @Column(name = "is_available")
    private boolean available;

    @Column(name = "quantity")
    private int quantity;
}