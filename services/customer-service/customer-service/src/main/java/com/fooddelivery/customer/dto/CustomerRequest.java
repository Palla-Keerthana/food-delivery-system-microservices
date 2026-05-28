package com.fooddelivery.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String phone;
    private String address;
}