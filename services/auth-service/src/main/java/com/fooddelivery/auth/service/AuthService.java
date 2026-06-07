package com.fooddelivery.auth.service;

import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;
import com.fooddelivery.auth.dto.UpdatePasswordRequest;

public interface AuthService {

    String register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    String updatePassword(Long userId, UpdatePasswordRequest request);
}