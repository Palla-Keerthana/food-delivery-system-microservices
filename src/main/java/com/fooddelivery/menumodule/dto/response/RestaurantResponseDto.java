package com.fooddelivery.menumodule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponseDto {
    private Long restaurantId;
    private Long userId;
    private String restaurantName;
    private String location;
    private String contactNumber;
}