package com.fooddelivery.auth.controller;

import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.entity.Role;
import com.fooddelivery.auth.exception.AuthenticationException;
import com.fooddelivery.auth.exception.GlobalExceptionHandler;
import com.fooddelivery.auth.exception.InvalidRequestException;
import com.fooddelivery.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthService authService;
    @InjectMocks AuthController authController;

    MockMvc mvc;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private RegisterRequest buildRegisterRequest(String email, String password,
                                                 Role role, String name) {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(password);
        req.setRole(role);
        req.setName(name);
        req.setPhone("9876543210");
        req.setAddress("Chennai, TN");
        return req;
    }

    private LoginRequest buildLoginRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    // ─── register ─────────────────────────────────────────────────────────────

    @Test
    void register_validCustomer_returns200() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "john@gmail.com", "Pass@123",
                                Role.CUSTOMER, "John"))))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_validRestaurantOwner_returns200() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        RegisterRequest req = buildRegisterRequest("resto@gmail.com",
                "Pass@123", Role.RESTAURANT_OWNER, "Owner");
        req.setRestaurantName("Spice Garden");
        req.setLocation("Chennai");
        req.setContactNumber("9876543210");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_validAgent_returns200() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "agent@gmail.com", "Pass@123",
                                Role.AGENT, "Ravi"))))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_missingEmail_returns400() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                null, "Pass@123", Role.CUSTOMER, "John"))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_missingPassword_returns400() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "john@gmail.com", null,
                                Role.CUSTOMER, "John"))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_missingName_returns400() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "john@gmail.com", "Pass@123",
                                Role.CUSTOMER, null))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_invalidEmailFormat_returns400() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "invalidemail", "Pass@123",
                                Role.CUSTOMER, "John"))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_emailAlreadyExists_returns400() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new InvalidRequestException("Email already registered"));

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(buildRegisterRequest(
                                "john@gmail.com", "Pass@123",
                                Role.CUSTOMER, "John"))))
                .andExpect(status().isBadRequest());
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        LoginResponse response = new LoginResponse(
                1L, "eyJhbGci...", "CUSTOMER", "john@gmail.com");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("john@gmail.com", "Pass@123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.token").value("eyJhbGci..."))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("john@gmail.com", "wrongPass"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingEmail_returns400() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest(null, "Pass@123"))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void login_missingPassword_returns400() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("john@gmail.com", null))))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void login_inactiveAccount_returns401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("User account is inactive"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("john@gmail.com", "Pass@123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_restaurantOwner_returns200WithToken() throws Exception {
        LoginResponse response = new LoginResponse(
                2L, "eyJhbGci...", "RESTAURANT_OWNER", "resto@gmail.com");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("resto@gmail.com", "Pass@123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.role").value("RESTAURANT_OWNER"));
    }

    @Test
    void login_agent_returns200WithToken() throws Exception {
        LoginResponse response = new LoginResponse(
                3L, "eyJhbGci...", "AGENT", "agent@gmail.com");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                buildLoginRequest("agent@gmail.com", "Pass@123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.role").value("AGENT"));
    }
}