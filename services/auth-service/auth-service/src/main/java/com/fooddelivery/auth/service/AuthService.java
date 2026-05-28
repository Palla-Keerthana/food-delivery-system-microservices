package com.fooddelivery.auth.service;

import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}