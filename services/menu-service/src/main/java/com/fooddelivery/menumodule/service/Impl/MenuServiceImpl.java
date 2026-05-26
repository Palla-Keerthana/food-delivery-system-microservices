package com.fooddelivery.menumodule.service.impl;

import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.entity.MenuItem;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.MenuItemNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MenuService interface.
 * Contains business logic for all menu item operations.
 * Validates input, converts DTOs to entities and delegates to repository.
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Validates input and saves new menu item to database.
     * New items are set as available by default.
     *
     * @param dto contains item name, description, price and restaurantId
     * @throws InvalidRequestException if name, price or restaurantId is invalid
     */
    @Override
    public void addMenuItem(MenuRequestDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Item name cannot be empty.");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            throw new InvalidRequestException("Price must be greater than zero.");
        }
        if (dto.getRestaurantId() == null) {
            throw new InvalidRequestException("Restaurant ID is required.");
        }
        MenuItem item = new MenuItem();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setRestaurantId(dto.getRestaurantId());
        item.setAvailable(true);
        menuItemRepository.save(item);
    }

    /**
     * Validates input, fetches existing item and updates with new details.
     * Uses JPA save() which updates when ID exists.
     *
     * @param itemId the ID of the item to update
     * @param dto    contains new name, description and price
     * @throws InvalidRequestException   if ID or fields are invalid
     * @throws MenuItemNotFoundException if item not found in database
     */
    @Override
    public void updateMenuItem(Long itemId, MenuRequestDto dto) {
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Item name cannot be empty.");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            throw new InvalidRequestException("Price must be greater than zero.");
        }
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(
                        "Menu item not found with ID: " + itemId));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setRestaurantId(dto.getRestaurantId());
        menuItemRepository.save(item);
    }

    /**
     * Validates item ID and permanently removes item from database.
     * Throws exception if item does not exist before deleting.
     *
     * @param itemId the ID of the menu item to delete
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found in database
     */
    @Override
    public void deleteMenuItem(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (!menuItemRepository.existsById(itemId)) {
            throw new MenuItemNotFoundException(
                    "Menu item not found with ID: " + itemId);
        }
        menuItemRepository.deleteById(itemId);
    }

    /**
     * Fetches all menu items for given restaurant and converts to DTOs.
     * Returns empty list if no items found for restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return list of MenuResponseDto for that restaurant
     * @throws InvalidRequestException if restaurantId is invalid
     */
    @Override
    public List<MenuResponseDto> getMenuByRestaurant(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidRequestException("Invalid restaurant ID.");
        }
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches single menu item by ID and converts to DTO.
     * Throws exception if item does not exist in database.
     *
     * @param itemId the ID of the menu item
     * @return MenuResponseDto with item details
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found in database
     */
    @Override
    public MenuResponseDto getMenuItemById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(
                        "Menu item not found with ID: " + itemId));
        return convertToDto(item);
    }

    /**
     * Fetches all items where available is true and quantity greater than zero.
     * Used by customers to view items they can order across all restaurants.
     *
     * @return list of available MenuResponseDto
     * @throws MenuItemNotFoundException if no available items found
     */
    @Override
    public List<MenuResponseDto> getAllAvailableItems() {
        List<MenuItem> items = menuItemRepository
                .findByAvailableTrueAndQuantityGreaterThan(0);
        if (items.isEmpty()) {
            throw new MenuItemNotFoundException(
                    "No available menu items found.");
        }
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches item by ID and updates availability status.
     * Restaurant owner uses this to toggle item ON or OFF.
     *
     * @param itemId the ID of the menu item
     * @param status true to mark available, false to mark unavailable
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found in database
     */
    @Override
    public void updateAvailability(Long itemId, boolean status) {
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(
                        "Menu item not found with ID: " + itemId));
        item.setAvailable(status);
        menuItemRepository.save(item);
    }


    /**
     * Converts MenuItem entity to MenuResponseDto.
     * Private helper used by all methods that return data to client.
     *
     * @param item the MenuItem entity from database
     * @return MenuResponseDto with fields copied from entity
     */
    private MenuResponseDto convertToDto(MenuItem item) {
        MenuResponseDto dto = new MenuResponseDto();
        dto.setItemId(item.getItemId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setRestaurantId(item.getRestaurantId());
        dto.setAvailable(item.isAvailable());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}