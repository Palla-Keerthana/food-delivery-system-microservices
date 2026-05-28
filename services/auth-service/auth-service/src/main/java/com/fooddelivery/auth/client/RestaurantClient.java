package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.RestaurantRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "menu-service")
public interface RestaurantClient {

    @PostMapping("/api/restaurants")
    void registerRestaurant(@RequestBody RestaurantRequest request);
}