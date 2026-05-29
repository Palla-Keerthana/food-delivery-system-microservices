package com.fooddelivery.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "menu-service")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants/{restaurantId}/location")
    Map<String, Object> getRestaurantLocation(
            @PathVariable Long restaurantId);
}