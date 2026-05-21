package com.fooddelivery.menumodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDto {
    private Long userId;
    private String restaurantName;
    private String location;
    private String contactNumber;
}