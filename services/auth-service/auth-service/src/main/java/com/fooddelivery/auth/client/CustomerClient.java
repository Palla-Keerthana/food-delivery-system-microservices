package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.CustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @PostMapping("/api/customers")
    void createProfile(@RequestBody CustomerRequest request);
}