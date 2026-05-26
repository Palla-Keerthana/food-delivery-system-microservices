package com.fooddelivery.menumodule.service.impl;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RestaurantService interface.
 * Contains business logic for all restaurant operations.
 * Validates input, converts DTOs to entities and delegates to repository.
 */
@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Validates input, converts DTO to entity and saves restaurant.
     * Called when a new Restaurant Owner completes registration.
     *
     * @param dto contains userId, restaurantName, location and contactNumber
     * @throws InvalidRequestException if any required field is empty or invalid
     */
    @Override
    public void registerRestaurant(RestaurantRequestDto dto) {
        if (dto.getRestaurantName() == null || dto.getRestaurantName().trim().isEmpty()) {
            throw new InvalidRequestException("Restaurant name cannot be empty.");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new InvalidRequestException("Location cannot be empty.");
        }
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            throw new InvalidRequestException("Contact number cannot be empty.");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setUserId(dto.getUserId());
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        restaurantRepository.save(restaurant);
    }

    /**
     * Fetches restaurant by owner's userId and converts to DTO.
     * Used after login to load the restaurant owner's details.
     *
     * @param userId the user ID of the restaurant owner
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if no restaurant found for userId
     */
    @Override
    public RestaurantResponseDto getByUserId(Long userId) {
        Restaurant restaurant = restaurantRepository.findByUserId(userId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found for user ID: " + userId));
        return convertToDto(restaurant);
    }

    /**
     * Fetches single restaurant by ID and converts to DTO.
     * Throws exception if restaurant does not exist in database.
     *
     * @param restaurantId the ID of the restaurant
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if restaurant not found in database
     */
    @Override
    public RestaurantResponseDto getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with ID: " + restaurantId));
        return convertToDto(restaurant);
    }

    /**
     * Fetches all restaurants and converts each to DTO.
     * Returns empty list if no restaurants exist in database.
     *
     * @return list of all RestaurantResponseDto
     */
    @Override
    public List<RestaurantResponseDto> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Validates ID, fetches restaurant and updates with new details.
     * Only updates name, location and contactNumber — userId never changes.
     *
     * @param restaurantId the ID of the restaurant to update
     * @param dto          contains new restaurantName, location and contactNumber
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found in database
     */
    @Override
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto dto) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidRequestException("Invalid restaurant ID.");
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with ID: " + restaurantId));
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        restaurantRepository.save(restaurant);
    }

    /**
     * Deletes all menu items of restaurant first then deletes restaurant.
     * Prevents foreign key constraint errors during deletion.
     *
     * @param restaurantId the ID of the restaurant to delete
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found in database
     */
    @Override
    public void deleteRestaurant(Long restaurantId) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidRequestException("Invalid restaurant ID.");
        }
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(
                    "Restaurant not found with ID: " + restaurantId);
        }
        menuItemRepository.deleteByRestaurantId(restaurantId);
        restaurantRepository.deleteById(restaurantId);
    }

    /**
     * Converts Restaurant entity to RestaurantResponseDto.
     * Private helper used by all methods that return data to client.
     *
     * @param r the Restaurant entity from database
     * @return RestaurantResponseDto with fields copied from entity
     */
    private RestaurantResponseDto convertToDto(Restaurant r) {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        dto.setRestaurantId(r.getRestaurantId());
        dto.setUserId(r.getUserId());
        dto.setRestaurantName(r.getRestaurantName());
        dto.setLocation(r.getLocation());
        dto.setContactNumber(r.getContactNumber());
        return dto;
    }
}