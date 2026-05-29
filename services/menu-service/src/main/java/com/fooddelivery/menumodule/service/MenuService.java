package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.MenuPatchDto;
import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.MenuItemNotFoundException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import java.util.List;

/**
 * Service interface for Menu Item operations.
 * Defines all business operations related to menu management.
 * Implemented by MenuServiceImpl.
 */
public interface MenuService {

    /**
     * Adds a new menu item to a restaurant's menu.
     *
     * @param dto contains item name, description, price and restaurantId
     * @throws InvalidRequestException     if name, price or restaurantId invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    void addMenuItem(MenuRequestDto dto)
            throws InvalidRequestException, RestaurantNotFoundException;

    /**
     * Updates an existing menu item with new details.
     *
     * @param itemId the ID of the item to update
     * @param dto    contains new name, description and price
     * @throws InvalidRequestException     if ID or fields are invalid
     * @throws MenuItemNotFoundException   if item not found
     * @throws RestaurantNotFoundException if restaurant not found
     */
    void updateMenuItem(Long itemId, MenuRequestDto dto)
            throws InvalidRequestException,
            MenuItemNotFoundException,
            RestaurantNotFoundException;

    /**
     * Permanently deletes a menu item from database.
     *
     * @param itemId the ID of the menu item to delete
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    void deleteMenuItem(Long itemId)
            throws InvalidRequestException, MenuItemNotFoundException;

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return list of MenuResponseDto for that restaurant
     * @throws InvalidRequestException if restaurantId is invalid
     */
    List<MenuResponseDto> getMenuByRestaurant(Long restaurantId)
            throws InvalidRequestException;

    /**
     * Retrieves a single menu item by its ID.
     *
     * @param itemId the ID of the menu item
     * @return MenuResponseDto with item details
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    MenuResponseDto getMenuItemById(Long itemId)
            throws InvalidRequestException, MenuItemNotFoundException;

    /**
     * Retrieves all available menu items across all restaurants.
     *
     * @return list of all available MenuResponseDto
     * @throws MenuItemNotFoundException if no available items found
     */
    List<MenuResponseDto> getAllAvailableItems()
            throws MenuItemNotFoundException;

    /**
     * Updates availability status of a menu item.
     *
     * @param itemId the ID of the menu item
     * @param status true to mark available, false to mark unavailable
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    void updateAvailability(Long itemId, boolean status)
            throws InvalidRequestException, MenuItemNotFoundException;

    /**
     * Partially updates a menu item with only provided fields.
     *
     * @param itemId   the ID of the menu item
     * @param patchDto contains only fields to update
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    void patchMenuItem(Long itemId, MenuPatchDto patchDto)
            throws InvalidRequestException, MenuItemNotFoundException;
    /**
     * Reduces stock quantity after order placed.
     *
     * @param itemId   the ID of menu item
     * @param quantity quantity to reduce
     * @throws InvalidRequestException if ID or quantity invalid
     */
    void reduceQuantity(Long itemId, int quantity)
            throws InvalidRequestException;
}