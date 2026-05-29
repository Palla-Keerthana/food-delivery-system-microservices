package com.fooddelivery.menumodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for partial update of Restaurant.
 * All fields are optional — only provided fields are updated.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantPatchDto {

    // All fields optional!
    private String restaurantName;
    private String location;
    private String contactNumber;
}