package com.fooddelivery.menumodule.service;

import com.fooddelivery.menumodule.dto.request.RestaurantPatchDto;
import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import java.util.List;

/**
 * Service interface for Restaurant operations.
 * Defines all business operations related to restaurant management.
 * Implemented by RestaurantServiceImpl.
 */
public interface RestaurantService {

    /**
     * Registers a new restaurant in the database.
     *
     * @param dto contains userId, restaurantName, location and contactNumber
     * @throws InvalidRequestException if any required field is empty
     */
    void registerRestaurant(RestaurantRequestDto dto)
            throws InvalidRequestException;

    /**
     * Retrieves restaurant by owner's user ID.
     *
     * @param userId the user ID of restaurant owner
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if no restaurant found for userId
     */
    RestaurantResponseDto getByUserId(Long userId)
            throws RestaurantNotFoundException;

    /**
     * Retrieves a single restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if restaurant not found
     */
    RestaurantResponseDto getRestaurant(Long restaurantId)
            throws RestaurantNotFoundException;

    /**
     * Retrieves all restaurants from database.
     *
     * @return list of all RestaurantResponseDto
     */
    List<RestaurantResponseDto> getAllRestaurants();

    /**
     * Updates existing restaurant details.
     *
     * @param restaurantId the ID of restaurant to update
     * @param dto          contains new name, location and contact
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    void updateRestaurant(Long restaurantId, RestaurantRequestDto dto)
            throws InvalidRequestException, RestaurantNotFoundException;

    /**
     * Partially updates a restaurant with only provided fields.
     *
     * @param restaurantId the ID of the restaurant
     * @param patchDto     contains only fields to update
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    void patchRestaurant(Long restaurantId, RestaurantPatchDto patchDto)
            throws InvalidRequestException, RestaurantNotFoundException;

    /**
     * Permanently deletes a restaurant from database.
     *
     * @param restaurantId the ID of restaurant to delete
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    void deleteRestaurant(Long restaurantId)
            throws InvalidRequestException, RestaurantNotFoundException;
}