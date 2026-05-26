package com.fooddelivery.menumodule.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Restaurant name cannot be empty")
    private String restaurantName;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @NotBlank(message = "Contact number cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Contact number must be 10 digits")
    private String contactNumber;
}