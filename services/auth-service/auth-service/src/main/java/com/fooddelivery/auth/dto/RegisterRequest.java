package com.fooddelivery.auth.dto;

import com.fooddelivery.auth.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private User.Role role;

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;
    private String address;

    // for restaurant
    private String restaurantName;
    private String location;
    private String contactNumber;

    // for agent
    private String vehicleType;
}