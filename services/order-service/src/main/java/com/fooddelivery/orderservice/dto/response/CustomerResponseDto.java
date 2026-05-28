package com.fooddelivery.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    private Long customerId;
    private Long userId;
    private String customerName;
    private String phone;
    private String address;
}