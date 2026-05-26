package com.fooddelivery.menumodule.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequestDto {

    @NotBlank(message = "Item name cannot be empty")

    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")

    private BigDecimal price;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}