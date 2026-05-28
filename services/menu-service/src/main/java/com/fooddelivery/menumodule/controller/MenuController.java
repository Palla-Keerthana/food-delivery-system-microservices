package com.fooddelivery.menumodule.controller;

import com.fooddelivery.menumodule.dto.request.MenuPatchDto;
import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("POST /api/menu - Adding menu item: {}", dto.getName());
        menuService.addMenuItem(dto);
        log.info("Menu item added successfully: {}", dto.getName());
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
        log.info("PUT /api/menu/{} - Updating menu item", itemId);
        menuService.updateMenuItem(itemId, dto);
        log.info("Menu item updated successfully with ID: {}", itemId);
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
        log.info("DELETE /api/menu/{} - Deleting menu item", itemId);
        menuService.deleteMenuItem(itemId);
        log.info("Menu item deleted successfully with ID: {}", itemId);
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
        log.info("GET /api/menu/{} - Fetching menu item", itemId);
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
        log.info("GET /api/menu/restaurant/{} - Fetching menu", restaurantId);
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
        log.info("GET /api/menu/available - Fetching all available items");
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
        log.info("GET /api/menu - Fetching all menu items");
        return ResponseEntity
                .ok(menuService.getAllAvailableItems());
    }

    /**
     * Reduces stock quantity after order is placed.
     * Called by Order Service via Feign Client.
     *
     * @param itemId   the ID of menu item
     * @param quantity quantity to reduce
     * @return 200 OK on success
     */
    @Operation(summary = "Reduce item quantity",
            description = "Called by Order Service after order placed")
    @PutMapping("/{itemId}/reduce")
    public ResponseEntity<String> reduceQuantity(
            @PathVariable Long itemId,
            @RequestParam("quantity") int quantity) {
        log.info("PUT /api/menu/{}/reduce - Reducing quantity by {}", itemId, quantity);
        menuService.reduceQuantity(itemId, quantity);
        log.info("Quantity reduced successfully for item ID: {}", itemId);
        return ResponseEntity.ok("Quantity reduced successfully!");
    }
    /**
     * PATCH /api/menu/{itemId}
     * Partially updates a menu item.
     * Only provided fields are updated — null fields ignored.
     *
     * @param itemId   the ID of menu item
     * @param patchDto contains only fields to update
     * @return 200 OK on success
     */
    @Operation(summary = "Partially update menu item",
            description = "Updates only provided fields of menu item")
    @PatchMapping("/{itemId}")
    public ResponseEntity<String> patchMenuItem(
            @PathVariable Long itemId,
            @RequestBody MenuPatchDto patchDto) {
        log.info("PATCH /api/menu/{} - Patching menu item", itemId);
        menuService.patchMenuItem(itemId, patchDto);
        log.info("Menu item patched successfully: {}", itemId);
        return ResponseEntity.ok("Menu item patched successfully!");
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
        log.info("PUT /api/menu/{}/availability - Setting status to {}", itemId, status);
        menuService.updateAvailability(itemId, status);
        log.info("Availability updated for item ID: {} to {}", itemId, status);
        return ResponseEntity
                .ok("Availability updated successfully!");
    }
}