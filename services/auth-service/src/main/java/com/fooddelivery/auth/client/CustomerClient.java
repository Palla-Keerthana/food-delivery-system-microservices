package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.CustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the customer-service.
 * Used to create customer profiles when a new user registers
 * with the role {@code CUSTOMER}.
 * Service is discovered via Eureka using the name CUSTOMER-SERVICE.
 */
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    /**
     * Sends a request to the customer-service to create a new customer profile.
     * Called during user registration when role is CUSTOMER.
     *
     * @param request the customer request containing userId, customerName,
     *                phone and address
     */
    @PostMapping("/api/customers")
    void createProfile(@RequestBody CustomerRequest request);
}