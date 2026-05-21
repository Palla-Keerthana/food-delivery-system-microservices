package com.fooddelivery.menumodule.controller;

import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Menu Item operations.
 * Handles all HTTP requests for menu management.
 * Base URL: /api/menu
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * POST /api/menu
     * Add a new menu item to a restaurant.
     * Returns 201 CREATED on success.
     */
    @PostMapping
    public ResponseEntity<String> addMenuItem(
            @RequestBody MenuRequestDto dto) {
        menuService.addMenuItem(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Menu item added successfully!");
    }

    /**
     * PUT /api/menu/{itemId}
     * Update an existing menu item.
     * Returns 200 OK on success.
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<String> updateMenuItem(
            @PathVariable Long itemId,
            @RequestBody MenuRequestDto dto) {
        menuService.updateMenuItem(itemId, dto);
        return ResponseEntity
                .ok("Menu item updated successfully!");
    }

    /**
     * DELETE /api/menu/{itemId}
     * Delete a menu item permanently.
     * Returns 200 OK on success.
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long itemId) {
        menuService.deleteMenuItem(itemId);
        return ResponseEntity
                .ok("Menu item deleted successfully!");
    }

    /**
     * GET /api/menu/{itemId}
     * Get a single menu item by ID.
     * Returns 200 OK with item details.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<MenuResponseDto> getMenuItemById(
            @PathVariable Long itemId) {
        return ResponseEntity
                .ok(menuService.getMenuItemById(itemId));
    }

    /**
     * GET /api/menu/restaurant/{restaurantId}
     * Get all menu items for a specific restaurant.
     * Returns 200 OK with list of items.
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuResponseDto>> getMenuByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity
                .ok(menuService.getMenuByRestaurant(restaurantId));
    }

    /**
     * GET /api/menu/available
     * Get all available menu items across all restaurants.
     * Returns 200 OK with list of available items.
     */
    @GetMapping("/available")
    public ResponseEntity<List<MenuResponseDto>> getAllAvailableItems() {
        return ResponseEntity
                .ok(menuService.getAllAvailableItems());
    }

    /**
     * GET /api/menu
     * Get all menu items.
     * Returns 200 OK with complete list.
     */
    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getAllMenuItems() {
        return ResponseEntity
                .ok(menuService.getAllAvailableItems());
    }

    /**
     * PUT /api/menu/{itemId}/availability?status=true/false
     * Toggle availability of a menu item.
     * Returns 200 OK on success.
     */
    @PutMapping("/{itemId}/availability")
    public ResponseEntity<String> updateAvailability(
            @PathVariable Long itemId,
            @RequestParam boolean status) {
        menuService.updateAvailability(itemId, status);
        return ResponseEntity
                .ok("Availability updated successfully!");
    }
}