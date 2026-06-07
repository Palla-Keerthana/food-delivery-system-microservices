package com.fooddelivery.auth.service.impl;

import com.fooddelivery.auth.client.AgentClient;
import com.fooddelivery.auth.client.CustomerClient;
import com.fooddelivery.auth.client.RestaurantClient;
import com.fooddelivery.auth.dto.AgentRequest;
import com.fooddelivery.auth.dto.CustomerRequest;
import com.fooddelivery.auth.dto.LoginRequest;
import com.fooddelivery.auth.dto.LoginResponse;
import com.fooddelivery.auth.dto.RegisterRequest;
import com.fooddelivery.auth.dto.RestaurantRequest;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.entity.Role;
import com.fooddelivery.auth.exception.AuthenticationException;
import com.fooddelivery.auth.exception.InvalidRequestException;
import com.fooddelivery.auth.repository.UserRepository;
import com.fooddelivery.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock JwtUtil jwtUtil;
    @Mock CustomerClient customerClient;
    @Mock RestaurantClient restaurantClient;
    @Mock AgentClient agentClient;
    @Mock BCryptPasswordEncoder passwordEncoder;

    @InjectMocks AuthServiceImpl authService;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(Long userId, String email,
                           String password, Role role, String status) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

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
    void register_customer_success_returnsMessage() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("hashedPass");
        User savedUser = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        String result = authService.register(buildRegisterRequest(
                "john@gmail.com", "Pass@123", Role.CUSTOMER, "John"));

        assertThat(result).isEqualTo("User registered successfully");
        verify(customerClient).createProfile(any(CustomerRequest.class));
        verify(restaurantClient, never()).registerRestaurant(any());
        verify(agentClient, never()).registerAgent(any());
    }

    @Test
    void register_restaurantOwner_success_callsRestaurantClient() {
        when(userRepository.existsByEmail("resto@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");
        User savedUser = buildUser(2L, "resto@gmail.com",
                "hashedPass", Role.RESTAURANT_OWNER, "ACTIVE");
        when(userRepository.save(any())).thenReturn(savedUser);

        RegisterRequest req = buildRegisterRequest(
                "resto@gmail.com", "Pass@123", Role.RESTAURANT_OWNER, "Owner");
        req.setRestaurantName("Spice Garden");
        req.setLocation("Chennai");
        req.setContactNumber("9876543210");

        String result = authService.register(req);

        assertThat(result).isEqualTo("User registered successfully");
        verify(restaurantClient).registerRestaurant(any(RestaurantRequest.class));
        verify(customerClient, never()).createProfile(any());
        verify(agentClient, never()).registerAgent(any());
    }

    @Test
    void register_agent_success_callsAgentClient() {
        when(userRepository.existsByEmail("agent@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");
        User savedUser = buildUser(3L, "agent@gmail.com",
                "hashedPass", Role.AGENT, "ACTIVE");
        when(userRepository.save(any())).thenReturn(savedUser);

        String result = authService.register(buildRegisterRequest(
                "agent@gmail.com", "Pass@123", Role.AGENT, "Ravi"));

        assertThat(result).isEqualTo("User registered successfully");
        verify(agentClient).registerAgent(any(AgentRequest.class));
        verify(customerClient, never()).createProfile(any());
        verify(restaurantClient, never()).registerRestaurant(any());
    }

    @Test
    void register_emailAlreadyExists_throwsInvalidRequestException() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(buildRegisterRequest(
                "john@gmail.com", "Pass@123", Role.CUSTOMER, "John")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
        verify(customerClient, never()).createProfile(any());
    }

    @Test
    void register_encodesPasswordBeforeSaving() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Pass@123")).thenReturn("$2a$10$hashedValue");
        User savedUser = buildUser(1L, "john@gmail.com",
                "$2a$10$hashedValue", Role.CUSTOMER, "ACTIVE");
        when(userRepository.save(any())).thenReturn(savedUser);

        authService.register(buildRegisterRequest(
                "john@gmail.com", "Pass@123", Role.CUSTOMER, "John"));

        verify(passwordEncoder).encode("Pass@123");
    }

    @Test
    void register_setsStatusToActive() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");
        User savedUser = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.save(any())).thenReturn(savedUser);

        authService.register(buildRegisterRequest(
                "john@gmail.com", "Pass@123", Role.CUSTOMER, "John"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void register_customerServiceDown_stillRegistersUser() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");
        User savedUser = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.save(any())).thenReturn(savedUser);
        doThrow(new RuntimeException("Service unavailable"))
                .when(customerClient).createProfile(any());

        String result = authService.register(buildRegisterRequest(
                "john@gmail.com", "Pass@123", Role.CUSTOMER, "John"));

        assertThat(result).isEqualTo("User registered successfully");
        verify(userRepository).save(any());
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsLoginResponse() {
        User user = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@123", "hashedPass")).thenReturn(true);
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER"))
                .thenReturn("eyJhbGci...");

        LoginResponse result = authService.login(
                buildLoginRequest("john@gmail.com", "Pass@123"));

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getToken()).isEqualTo("eyJhbGci...");
        assertThat(result.getRole()).isEqualTo("CUSTOMER");
        assertThat(result.getEmail()).isEqualTo("john@gmail.com");
    }

    @Test
    void login_wrongPassword_throwsAuthenticationException() {
        User user = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                buildLoginRequest("john@gmail.com", "wrongPass")))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_userNotFound_throwsAuthenticationException() {
        when(userRepository.findByEmail("notfound@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                buildLoginRequest("notfound@gmail.com", "Pass@123")))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_inactiveAccount_throwsAuthenticationException() {
        User user = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "INACTIVE");
        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@123", "hashedPass")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(
                buildLoginRequest("john@gmail.com", "Pass@123")))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("inactive");

        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void login_generatesTokenWithCorrectEmailAndRole() {
        User user = buildUser(1L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@123", "hashedPass")).thenReturn(true);
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER"))
                .thenReturn("eyJhbGci...");

        authService.login(buildLoginRequest("john@gmail.com", "Pass@123"));

        verify(jwtUtil).generateToken("john@gmail.com", "CUSTOMER");
    }

    @Test
    void login_returnsCorrectUserId() {
        User user = buildUser(5L, "john@gmail.com",
                "hashedPass", Role.CUSTOMER, "ACTIVE");
        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass@123", "hashedPass")).thenReturn(true);
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER"))
                .thenReturn("eyJhbGci...");

        LoginResponse result = authService.login(
                buildLoginRequest("john@gmail.com", "Pass@123"));

        assertThat(result.getUserId()).isEqualTo(5L);
    }
}
