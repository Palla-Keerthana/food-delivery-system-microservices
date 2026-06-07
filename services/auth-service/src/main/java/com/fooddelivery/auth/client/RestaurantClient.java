package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.RestaurantRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the menu-service.
 * Used to create restaurant profiles when a new user registers
 * with the role {@code RESTAURANT_OWNER}.
 * Service is discovered via Eureka using the name MENU-SERVICE.
 */
@FeignClient(name = "MENU-SERVICE")
public interface RestaurantClient {

    /**
     * Sends a request to the menu-service to create a new restaurant profile.
     * Called during user registration when role is RESTAURANT_OWNER.
     *
     * @param request the restaurant request containing userId, restaurantName,
     *                location and contactNumber
     */
    @PostMapping("/api/restaurants")
    void registerRestaurant(@RequestBody RestaurantRequest request);
}