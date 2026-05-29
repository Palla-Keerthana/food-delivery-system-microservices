package com.fooddelivery.menumodule.service.Impl;

import com.fooddelivery.menumodule.dto.request.MenuPatchDto;
import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.entity.MenuItem;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.MenuItemNotFoundException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MenuService interface.
 * Contains business logic for all menu item operations.
 * Validates input, converts DTOs to entities and delegates to repository.
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;



    /**
     * Validates input and saves new menu item to database.
     * Fetches Restaurant object using restaurantId before saving.
     * New items are set as available by default.
     *
     * @param dto contains item name, description, price and restaurantId
     * @throws InvalidRequestException     if name, price or restaurantId invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public void addMenuItem(MenuRequestDto dto)
            throws InvalidRequestException, RestaurantNotFoundException {
        log.info("Adding menu item: {}", dto.getName());

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            log.warn("Item name is empty!");
            throw new InvalidRequestException("Item name cannot be empty.");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            log.warn("Invalid price: {}", dto.getPrice());
            throw new InvalidRequestException("Price must be greater than zero.");
        }
        if (dto.getRestaurantId() == null) {
            log.warn("Restaurant ID is null!");
            throw new InvalidRequestException("Restaurant ID is required.");
        }

        Restaurant restaurant = restaurantRepository
                .findById(dto.getRestaurantId())
                .orElseThrow(() -> {
                    log.error("Restaurant not found with ID: {}", dto.getRestaurantId());
                    return new RestaurantNotFoundException(
                            "Restaurant not found with ID: " + dto.getRestaurantId());
                });

        MenuItem item = new MenuItem();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setRestaurant(restaurant);
        item.setAvailable(true);
        item.setQuantity(dto.getQuantity());
        menuItemRepository.save(item);

        log.info("Menu item saved successfully: {}", dto.getName());
    }

    /**
     * Validates input, fetches existing item and updates with new details.
     * Fetches Restaurant object if restaurantId provided in dto.
     *
     * @param itemId the ID of the item to update
     * @param dto    contains new name, description and price
     * @throws InvalidRequestException     if ID or fields are invalid
     * @throws MenuItemNotFoundException   if item not found
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public void updateMenuItem(Long itemId, MenuRequestDto dto)
            throws InvalidRequestException,
            MenuItemNotFoundException,
            RestaurantNotFoundException {
        log.info("Updating menu item with ID: {}", itemId);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            log.warn("Item name is empty!");
            throw new InvalidRequestException("Item name cannot be empty.");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            log.warn("Invalid price: {}", dto.getPrice());
            throw new InvalidRequestException("Price must be greater than zero.");
        }

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Menu item not found with ID: {}", itemId);
                    return new MenuItemNotFoundException(
                            "Menu item not found with ID: " + itemId);
                });

        if (dto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository
                    .findById(dto.getRestaurantId())
                    .orElseThrow(() -> {
                        log.error("Restaurant not found with ID: {}", dto.getRestaurantId());
                        return new RestaurantNotFoundException(
                                "Restaurant not found with ID: " + dto.getRestaurantId());
                    });
            item.setRestaurant(restaurant);
        }

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        menuItemRepository.save(item);

        log.info("Menu item updated successfully with ID: {}", itemId);
    }

    /**
     * Validates item ID and permanently removes item from database.
     *
     * @param itemId the ID of the menu item to delete
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    @Override
    public void deleteMenuItem(Long itemId)
            throws InvalidRequestException, MenuItemNotFoundException {
        log.info("Deleting menu item with ID: {}", itemId);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (!menuItemRepository.existsById(itemId)) {
            log.error("Menu item not found with ID: {}", itemId);
            throw new MenuItemNotFoundException(
                    "Menu item not found with ID: " + itemId);
        }

        menuItemRepository.deleteById(itemId);
        log.info("Menu item deleted successfully with ID: {}", itemId);
    }

    /**
     * Fetches all menu items for given restaurant and converts to DTOs.
     *
     * @param restaurantId the ID of the restaurant
     * @return list of MenuResponseDto for that restaurant
     * @throws InvalidRequestException if restaurantId is invalid
     */
    @Override
    public List<MenuResponseDto> getMenuByRestaurant(Long restaurantId)
            throws InvalidRequestException {
        log.info("Fetching menu for restaurant ID: {}", restaurantId);

        if (restaurantId == null || restaurantId <= 0) {
            log.warn("Invalid restaurant ID: {}", restaurantId);
            throw new InvalidRequestException("Invalid restaurant ID.");
        }

        List<MenuResponseDto> items = menuItemRepository
                .findByRestaurant_RestaurantId(restaurantId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.info("Found {} items for restaurant ID: {}", items.size(), restaurantId);
        return items;
    }

    /**
     * Fetches single menu item by ID and converts to DTO.
     *
     * @param itemId the ID of the menu item
     * @return MenuResponseDto with item details
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    @Override
    public MenuResponseDto getMenuItemById(Long itemId)
            throws InvalidRequestException, MenuItemNotFoundException {
        log.info("Fetching menu item with ID: {}", itemId);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Menu item not found with ID: {}", itemId);
                    return new MenuItemNotFoundException(
                            "Menu item not found with ID: " + itemId);
                });

        log.info("Menu item found: {}", item.getName());
        return convertToDto(item);
    }

    /**
     * Fetches all items where available is true and quantity greater than zero.
     *
     * @return list of available MenuResponseDto
     * @throws MenuItemNotFoundException if no available items found
     */
    @Override
    public List<MenuResponseDto> getAllAvailableItems()
            throws MenuItemNotFoundException {
        log.info("Fetching all available menu items");

        List<MenuItem> items = menuItemRepository
                .findByAvailableTrueAndQuantityGreaterThan(0);

        if (items.isEmpty()) {
            log.warn("No available menu items found!");
            throw new MenuItemNotFoundException(
                    "No available menu items found.");
        }

        log.info("Found {} available items", items.size());
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches item by ID and updates availability status.
     *
     * @param itemId the ID of the menu item
     * @param status true to mark available, false to mark unavailable
     * @throws InvalidRequestException   if ID is invalid
     * @throws MenuItemNotFoundException if item not found
     */
    @Override
    public void updateAvailability(Long itemId, boolean status)
            throws InvalidRequestException, MenuItemNotFoundException {
        log.info("Updating availability for item ID: {} to {}", itemId, status);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Menu item not found with ID: {}", itemId);
                    return new MenuItemNotFoundException(
                            "Menu item not found with ID: " + itemId);
                });

        item.setAvailable(status);
        menuItemRepository.save(item);
        log.info("Availability updated for item ID: {} to {}", itemId, status);
    }

    /**
     * Partially updates menu item with only provided fields.
     * Null fields are ignored — not updated.
     *
     * @param itemId   the ID of menu item
     * @param patchDto contains only fields to update
     * @throws InvalidRequestException   if ID invalid
     * @throws MenuItemNotFoundException if item not found
     */
    @Override
    public void patchMenuItem(Long itemId, MenuPatchDto patchDto)
            throws InvalidRequestException, MenuItemNotFoundException {
        log.info("PATCH menu item with ID: {}", itemId);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Menu item not found with ID: {}", itemId);
                    return new MenuItemNotFoundException(
                            "Menu item not found with ID: " + itemId);
                });

        if (patchDto.getName() != null &&
                !patchDto.getName().trim().isEmpty()) {
            item.setName(patchDto.getName());
        }
        if (patchDto.getDescription() != null) {
            item.setDescription(patchDto.getDescription());
        }
        if (patchDto.getPrice() != null &&
                patchDto.getPrice().doubleValue() > 0) {
            item.setPrice(patchDto.getPrice());
        }
        if (patchDto.getQuantity() != null &&
                patchDto.getQuantity() > 0) {
            item.setQuantity(patchDto.getQuantity());
        }
        if (patchDto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository
                    .findById(patchDto.getRestaurantId())
                    .orElseThrow(() -> new RestaurantNotFoundException(
                            "Restaurant not found with ID: "
                                    + patchDto.getRestaurantId()));
            item.setRestaurant(restaurant);
        }

        menuItemRepository.save(item);
        log.info("Menu item patched successfully with ID: {}", itemId);
    }

    /**
     * Validates input and reduces stock quantity.
     *
     * @param itemId   the ID of menu item
     * @param quantity quantity to reduce
     * @throws InvalidRequestException if ID or quantity invalid
     */
    @Override
    public void reduceQuantity(Long itemId, int quantity)
            throws InvalidRequestException {
        log.info("Reducing quantity for item ID: {} by {}", itemId, quantity);

        if (itemId == null || itemId <= 0) {
            log.warn("Invalid item ID: {}", itemId);
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new InvalidRequestException(
                    "Quantity must be greater than zero.");
        }

        menuItemRepository.reduceQuantity(itemId, quantity);
        log.info("Quantity reduced successfully for item ID: {}", itemId);
    }


    /**
     * Converts MenuItem entity to MenuResponseDto.
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
        if (item.getRestaurant() != null) {
            dto.setRestaurantId(item.getRestaurant().getRestaurantId());
        }
        dto.setAvailable(item.isAvailable());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}