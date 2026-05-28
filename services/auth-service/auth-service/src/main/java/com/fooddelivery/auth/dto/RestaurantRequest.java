package com.fooddelivery.auth.dto;

import lombok.Data;

@Data
public class RestaurantRequest {
    private Long userId;
    private String restaurantName;
    private String location;
    private String contactNumber;
}