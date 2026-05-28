package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
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
     */
    void addMenuItem(MenuRequestDto dto);

    /**
     * Updates an existing menu item with new details.
     *
     * @param itemId the ID of the item to update
     * @param dto    contains new name, description and price
     */
    void updateMenuItem(Long itemId, MenuRequestDto dto);

    /**
     * Permanently deletes a menu item from the database.
     *
     * @param itemId the ID of the menu item to delete
     */
    void deleteMenuItem(Long itemId);

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return list of MenuResponseDto for that restaurant
     */
    List<MenuResponseDto> getMenuByRestaurant(Long restaurantId);

    /**
     * Retrieves a single menu item by its ID.
     *
     * @param itemId the ID of the menu item
     * @return MenuResponseDto with item details
     */
    MenuResponseDto getMenuItemById(Long itemId);

    /**
     * Retrieves all available menu items across all restaurants.
     * Only returns items where is_available is true AND quantity greater than 0.
     *
     * @return list of all available MenuResponseDto
     */
    List<MenuResponseDto> getAllAvailableItems();

    /**
     * Updates the availability status of a menu item.
     *
     * @param itemId the ID of the menu item
     * @param status true to mark available, false to mark unavailable
     */
    void updateAvailability(Long itemId, boolean status);

    void reduceQuantity(Long itemId, int quantity);
}