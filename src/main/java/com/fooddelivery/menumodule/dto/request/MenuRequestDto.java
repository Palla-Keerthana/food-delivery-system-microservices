package com.fooddelivery.menumodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequestDto {

    private String name;           // item name from user input
    private String description;    // item description
    private BigDecimal price;      // item price
    private Long restaurantId;     // which restaurant this item belongs to
}