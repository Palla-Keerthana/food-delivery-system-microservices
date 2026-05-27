package com.fooddelivery.orderservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDto {

    private Long itemId;
    private String name;
    private BigDecimal price;
    private boolean available;  // ← your serviceimpl uses menuItem.isAvailable()
    private int quantity;
}