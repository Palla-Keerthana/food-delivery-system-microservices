package com.fooddelivery.auth.dto;

import lombok.Data;

@Data
public class CustomerRequest {
    private Long userId;
    private String customerName;
    private String phone;
    private String address;
}