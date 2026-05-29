package com.fooddelivery.orderservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDto {

    private Long itemId;           // item ID — shown to user
    private String name;           // item name
    private String description;    // item description
    private BigDecimal price;      // item price
    private Long restaurantId;     // which restaurant
    private boolean available;     // is item available or not
    private int quantity;          // stock quantity
}

