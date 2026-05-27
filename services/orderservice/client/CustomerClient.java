package com.fooddelivery.orderservice.client;

import com.fooddelivery.orderservice.dto.response.CustomerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
// ↑ must match spring.application.name in customer-service's application.properties
public interface CustomerClient {

    @GetMapping("/api/customers/{customerId}")
    CustomerResponseDto getCustomerById(@PathVariable("customerId") Long customerId);
}