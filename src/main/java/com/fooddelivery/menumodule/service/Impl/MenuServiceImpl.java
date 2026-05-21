package com.fooddelivery.menumodule.service.Impl;

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

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    public void addMenuItem(MenuRequestDto dto) {
        // ✅ validate input
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
        item.setAvailable(true);// new item always available by default
        item.setQuantity(10);    // ← add default quantity!
        menuItemRepository.save(item);
    }

    @Override
    public void updateMenuItem(Long itemId, MenuRequestDto dto) {
        // ✅ validate input
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Item name cannot be empty.");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            throw new InvalidRequestException("Price must be greater than zero.");
        }

        // ✅ check if item exists first
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(
                        "Menu item not found with ID: " + itemId));

        // ✅ update fields
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setRestaurantId(dto.getRestaurantId());
        menuItemRepository.save(item); // JPA save() updates if ID exists!
    }

    @Override
    public void deleteMenuItem(Long itemId) {
        // ✅ validate input
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        // ✅ check if exists before deleting
        if (!menuItemRepository.existsById(itemId)) {
            throw new MenuItemNotFoundException(
                    "Menu item not found with ID: " + itemId);
        }
        menuItemRepository.deleteById(itemId); // JPA deleteById!
    }

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

    @Override
    public List<MenuResponseDto> getAllAvailableItems() {
        List<MenuItem> items = menuItemRepository
                .findByAvailableTrueAndQuantityGreaterThan(0);
        // ✅ return empty list instead of throwing exception
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAvailability(Long itemId, boolean status) {
        if (itemId == null || itemId <= 0) {
            throw new InvalidRequestException("Invalid item ID.");
        }
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new MenuItemNotFoundException(
                        "Menu item not found with ID: " + itemId));
        item.setAvailable(status);
        menuItemRepository.save(item); // JPA save() updates!
    }

    // ✅ private helper — converts Entity to ResponseDto
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