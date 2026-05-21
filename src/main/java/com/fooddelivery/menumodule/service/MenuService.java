package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import java.util.List;

public interface MenuService {

    void addMenuItem(MenuRequestDto dto);

    void updateMenuItem(Long itemId, MenuRequestDto dto);

    void deleteMenuItem(Long itemId);

    List<MenuResponseDto> getMenuByRestaurant(Long restaurantId);

    MenuResponseDto getMenuItemById(Long itemId);

    List<MenuResponseDto> getAllAvailableItems();

    void updateAvailability(Long itemId, boolean status);
}