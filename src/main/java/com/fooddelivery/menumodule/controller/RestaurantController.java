package com.fooddelivery.menumodule.controller;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Restaurant operations.
 * Handles all HTTP requests related to restaurant management.
 * Provides endpoints for registering, updating, deleting and viewing restaurants.
 * Base URL: /api/restaurants
 */
@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Controller",
        description = "APIs for managing restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Registers a new restaurant in the system.
     * Called when a Restaurant Owner completes registration.
     *
     * @param dto contains userId, restaurantName, location and contactNumber
     * @return 201 CREATED with success message
     */
    @Operation(summary = "Register new restaurant",
            description = "Registers a new restaurant for a restaurant owner")
    @PostMapping
    public ResponseEntity<String> registerRestaurant(
            @Valid @RequestBody RestaurantRequestDto dto) {
        restaurantService.registerRestaurant(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Restaurant registered successfully!");
    }

    /**
     * Retrieves all restaurants from the database.
     * Accessible by all users.
     *
     * @return 200 OK with list of RestaurantResponseDto
     */
    @Operation(summary = "Get all restaurants",
            description = "Returns list of all registered restaurants")
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants() {
        return ResponseEntity
                .ok(restaurantService.getAllRestaurants());
    }

    /**
     * Retrieves a single restaurant by its ID.
     * Accessible by all users.
     *
     * @param restaurantId the ID of the restaurant to fetch
     * @return 200 OK with RestaurantResponseDto
     */
    @Operation(summary = "Get restaurant by ID",
            description = "Fetch a single restaurant by its ID")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity
                .ok(restaurantService.getRestaurant(restaurantId));
    }

    /**
     * Retrieves restaurant details by the owner's user ID.
     * Used after login to load the restaurant owner's restaurant.
     *
     * @param userId the user ID of the restaurant owner
     * @return 200 OK with RestaurantResponseDto
     */
    @Operation(summary = "Get restaurant by user ID",
            description = "Fetch restaurant details using the owner's user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantByUserId(
            @PathVariable Long userId) {
        return ResponseEntity
                .ok(restaurantService.getByUserId(userId));
    }

    /**
     * Updates an existing restaurant's details.
     * Only the restaurant owner can update their restaurant.
     *
     * @param restaurantId the ID of the restaurant to update
     * @param dto          contains new restaurantName, location and contactNumber
     * @return 200 OK with success message
     */
    @Operation(summary = "Update restaurant",
            description = "Restaurant owner updates their restaurant details")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<String> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestBody RestaurantRequestDto dto) {
        restaurantService.updateRestaurant(restaurantId, dto);
        return ResponseEntity
                .ok("Restaurant updated successfully!");
    }

    /**
     * Permanently deletes a restaurant from the database.
     * All menu items of the restaurant are deleted first.
     * Only the restaurant owner can delete their restaurant.
     *
     * @param restaurantId the ID of the restaurant to delete
     * @return 200 OK with success message
     */
    @Operation(summary = "Delete restaurant",
            description = "Permanently deletes a restaurant and all its menu items")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity
                .ok("Restaurant deleted successfully!");
    }
}