package com.fooddelivery.orderservice.client;

import com.fooddelivery.orderservice.dto.response.CustomerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    // matches GET /api/customers/{customerId} in CustomerController
    @GetMapping("/api/customers/{customerId}")
    CustomerResponseDto getCustomerById(@PathVariable("customerId") Long customerId);
}