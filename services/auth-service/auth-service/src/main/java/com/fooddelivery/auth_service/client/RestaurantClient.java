package com.fooddelivery.auth_service.client;

import com.fooddelivery.auth_service.dto.RestaurantRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "menu-service")
public interface RestaurantClient {

    @PostMapping("/api/restaurants")
    void registerRestaurant(@RequestBody RestaurantRequest request);
}