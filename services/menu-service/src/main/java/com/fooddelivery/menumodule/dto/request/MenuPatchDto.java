package com.fooddelivery.menumodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for partial update of MenuItem.
 * All fields are optional — only provided fields are updated.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPatchDto {

    // All fields optional — null means don't update!
    private String name;
    private String description;
    private BigDecimal price;
    private Long restaurantId;
    private Integer quantity;
}