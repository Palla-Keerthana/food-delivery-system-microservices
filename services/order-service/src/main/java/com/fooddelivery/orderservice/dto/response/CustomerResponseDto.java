package com.fooddelivery.orderservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

    private Long customerId;
    private String customerName;
    private String phone;
    private String address;
}