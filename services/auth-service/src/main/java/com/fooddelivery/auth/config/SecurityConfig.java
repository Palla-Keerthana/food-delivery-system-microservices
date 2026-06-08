package com.fooddelivery.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the auth-service.
 * Configures Spring Security to be stateless (JWT-based),
 * disables CSRF and CORS, and permits all requests since
 * JWT validation is handled centrally at the API Gateway.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Provides a BCryptPasswordEncoder bean for hashing
     * and verifying user passwords.
     * Used by {@code AuthServiceImpl} during registration and login.
     *
     * @return a new instance of {@link BCryptPasswordEncoder}
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for the auth-service.
     * <ul>
     *   <li>Disables CSRF — not needed for stateless REST APIs</li>
     *   <li>Disables CORS — handled at API Gateway level</li>
     *   <li>Permits all requests — JWT validation done at API Gateway</li>
     *   <li>Stateless session — no server-side session stored</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}