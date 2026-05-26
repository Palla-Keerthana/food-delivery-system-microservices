package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import java.util.List;

/**
 * Service interface for Restaurant operations.
 * Defines all business operations related to restaurant management.
 * Implemented by RestaurantServiceImpl.
 */
public interface RestaurantService {

    /**
     * Registers a new restaurant in the database.
     * Called when a new Restaurant Owner completes registration.
     *
     * @param dto contains userId, restaurantName, location and contactNumber
     */
    void registerRestaurant(RestaurantRequestDto dto);

    /**
     * Retrieves restaurant details by the owner's user ID.
     * Used after login to load restaurant owner's restaurant.
     *
     * @param userId the user ID of the restaurant owner
     * @return RestaurantResponseDto with restaurant details
     */
    RestaurantResponseDto getByUserId(Long userId);

    /**
     * Retrieves a single restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return RestaurantResponseDto with restaurant details
     */
    RestaurantResponseDto getRestaurant(Long restaurantId);

    /**
     * Retrieves all restaurants from the database.
     *
     * @return list of all RestaurantResponseDto
     */
    List<RestaurantResponseDto> getAllRestaurants();

    /**
     * Updates an existing restaurant's details.
     *
     * @param restaurantId the ID of the restaurant to update
     * @param dto          contains new restaurantName, location and contactNumber
     */
    void updateRestaurant(Long restaurantId, RestaurantRequestDto dto);

    /**
     * Permanently deletes a restaurant from the database.
     * After deletion restaurant owner is logged out automatically.
     *
     * @param restaurantId the ID of the restaurant to delete
     */
    void deleteRestaurant(Long restaurantId);
}