package com.fooddelivery.auth_service.dto;

import lombok.Data;

@Data
public class RestaurantRequest {
    private Long userId;
    private String restaurantName;
    private String location;
    private String contactNumber;
}