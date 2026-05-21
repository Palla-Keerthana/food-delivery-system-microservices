package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import java.util.List;

public interface RestaurantService {

    void registerRestaurant(RestaurantRequestDto dto);

    RestaurantResponseDto getByUserId(Long userId);

    RestaurantResponseDto getRestaurant(Long restaurantId);

    List<RestaurantResponseDto> getAllRestaurants();

    void updateRestaurant(Long restaurantId, RestaurantRequestDto dto);

    void deleteRestaurant(Long restaurantId);
}