package com.fooddelivery.menumodule.controller;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Restaurant operations.
 * Handles all HTTP requests for restaurant management.
 * Base URL: /api/restaurants
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * POST /api/restaurants
     * Register a new restaurant.
     * Returns 201 CREATED on success.
     */
    @PostMapping
    public ResponseEntity<String> registerRestaurant(
            @RequestBody RestaurantRequestDto dto) {
        restaurantService.registerRestaurant(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Restaurant registered successfully!");
    }

    /**
     * GET /api/restaurants
     * Get all restaurants.
     * Returns 200 OK with list of restaurants.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants() {
        return ResponseEntity
                .ok(restaurantService.getAllRestaurants());
    }

    /**
     * GET /api/restaurants/{restaurantId}
     * Get a single restaurant by ID.
     * Returns 200 OK with restaurant details.
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity
                .ok(restaurantService.getRestaurant(restaurantId));
    }

    /**
     * GET /api/restaurants/user/{userId}
     * Get restaurant by owner's user ID.
     * Returns 200 OK with restaurant details.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantByUserId(
            @PathVariable Long userId) {
        return ResponseEntity
                .ok(restaurantService.getByUserId(userId));
    }

    /**
     * PUT /api/restaurants/{restaurantId}
     * Update restaurant details.
     * Returns 200 OK on success.
     */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<String> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantRequestDto dto) {
        restaurantService.updateRestaurant(restaurantId, dto);
        return ResponseEntity
                .ok("Restaurant updated successfully!");
    }

    /**
     * DELETE /api/restaurants/{restaurantId}
     * Delete a restaurant permanently.
     * Returns 200 OK on success.
     */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity
                .ok("Restaurant deleted successfully!");

    }
}