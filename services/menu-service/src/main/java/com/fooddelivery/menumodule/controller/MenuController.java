package com.fooddelivery.menumodule.controller;

import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Menu Item operations.
 * Handles all HTTP requests related to menu management.
 * Provides endpoints for adding, updating, deleting and viewing menu items.
 * Base URL: /api/menu
 */
@RestController
@RequestMapping("/api/menu")
@Tag(name = "Menu Controller",
        description = "APIs for managing menu items")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * Adds a new menu item to a restaurant's menu.
     * Only Restaurant Owner can perform this operation.
     *
     * @param dto contains item name, description, price and restaurantId
     * @return 201 CREATED with success message
     */
    @Operation(summary = "Add new menu item",
            description = "Restaurant owner adds a new item to menu")
    @PostMapping
    public ResponseEntity<String> addMenuItem(
            @Valid @RequestBody MenuRequestDto dto) {
        menuService.addMenuItem(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Menu item added successfully!");
    }

    /**
     * Updates an existing menu item with new details.
     * Only Restaurant Owner can perform this operation.
     *
     * @param itemId the ID of the menu item to update
     * @param dto    contains new name, description and price
     * @return 200 OK with success message
     */
    @Operation(summary = "Update menu item",
            description = "Restaurant owner updates an existing menu item")
    @PutMapping("/{itemId}")
    public ResponseEntity<String> updateMenuItem(
            @PathVariable Long itemId,
            @Valid @RequestBody MenuRequestDto dto) {
        menuService.updateMenuItem(itemId, dto);
        return ResponseEntity
                .ok("Menu item updated successfully!");
    }

    /**
     * Permanently deletes a menu item from the database.
     * Only Restaurant Owner can perform this operation.
     *
     * @param itemId the ID of the menu item to delete
     * @return 200 OK with success message
     */
    @Operation(summary = "Delete menu item",
            description = "Restaurant owner deletes a menu item permanently")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long itemId) {
        menuService.deleteMenuItem(itemId);
        return ResponseEntity
                .ok("Menu item deleted successfully!");
    }

    /**
     * Retrieves a single menu item by its ID.
     * Accessible by all users.
     *
     * @param itemId the ID of the menu item to fetch
     * @return 200 OK with MenuResponseDto
     */
    @Operation(summary = "Get menu item by ID",
            description = "Fetch a single menu item by its ID")
    @GetMapping("/{itemId}")
    public ResponseEntity<MenuResponseDto> getMenuItemById(
            @PathVariable Long itemId) {
        return ResponseEntity
                .ok(menuService.getMenuItemById(itemId));
    }

    /**
     * Retrieves all menu items for a specific restaurant.
     * Accessible by all users.
     *
     * @param restaurantId the ID of the restaurant
     * @return 200 OK with list of MenuResponseDto
     */
    @Operation(summary = "Get menu by restaurant",
            description = "Fetch all menu items for a specific restaurant")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuResponseDto>> getMenuByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity
                .ok(menuService.getMenuByRestaurant(restaurantId));
    }

    /**
     * Retrieves all available menu items across all restaurants.
     * Only returns items where is_available is true AND quantity greater than 0.
     * Accessible by all users — no token required.
     *
     * @return 200 OK with list of available MenuResponseDto
     */
    @Operation(summary = "Get all available items",
            description = "Returns all available menu items across all restaurants")
    @GetMapping("/available")
    public ResponseEntity<List<MenuResponseDto>> getAllAvailableItems() {
        return ResponseEntity
                .ok(menuService.getAllAvailableItems());
    }

    /**
     * Retrieves all menu items.
     * Accessible by all users.
     *
     * @return 200 OK with complete list of MenuResponseDto
     */
    @Operation(summary = "Get all menu items",
            description = "Returns complete list of all menu items")
    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getAllMenuItems() {
        return ResponseEntity
                .ok(menuService.getAllAvailableItems());
    }

    /**
     * Updates the availability status of a menu item.
     * Restaurant owner can toggle item ON or OFF.
     *
     * @param itemId the ID of the menu item
     * @param status true to mark available, false to mark unavailable
     * @return 200 OK with success message
     */
    @Operation(summary = "Toggle item availability",
            description = "Restaurant owner toggles menu item availability on or off")
    @PutMapping("/{itemId}/availability")
    public ResponseEntity<String> updateAvailability(
            @PathVariable Long itemId,
            @RequestParam boolean status) {
        menuService.updateAvailability(itemId, status);
        return ResponseEntity
                .ok("Availability updated successfully!");
    }
}