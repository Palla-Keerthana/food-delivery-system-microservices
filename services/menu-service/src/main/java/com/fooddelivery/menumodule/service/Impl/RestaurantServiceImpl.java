package com.fooddelivery.menumodule.service.Impl;

import com.fooddelivery.menumodule.dto.request.RestaurantPatchDto;
import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of RestaurantService interface.
 * Contains business logic for all restaurant operations.
 * Validates input, converts DTOs to entities and delegates to repository.
 */
@Slf4j
@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Validates input, converts DTO to entity and saves restaurant.
     *
     * @param dto contains userId, restaurantName, location and contactNumber
     * @throws InvalidRequestException if any required field is empty
     */
    @Override
    public void registerRestaurant(RestaurantRequestDto dto)
            throws InvalidRequestException {
        log.info("Registering new restaurant: {}", dto.getRestaurantName());

        if (restaurantRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new InvalidRequestException(
                    "User already has a registered restaurant!");
        }
        if (dto.getRestaurantName() == null || dto.getRestaurantName().trim().isEmpty()) {
            log.warn("Restaurant name is empty!");
            throw new InvalidRequestException("Restaurant name cannot be empty.");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            log.warn("Location is empty!");
            throw new InvalidRequestException("Location cannot be empty.");
        }
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            log.warn("Contact number is empty!");
            throw new InvalidRequestException("Contact number cannot be empty.");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setUserId(dto.getUserId());
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        restaurantRepository.save(restaurant);

        log.info("Restaurant registered successfully: {}", dto.getRestaurantName());
    }

    /**
     * Fetches restaurant by owner's userId and converts to DTO.
     *
     * @param userId the user ID of restaurant owner
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if no restaurant found for userId
     */
    @Override
    public RestaurantResponseDto getByUserId(Long userId)
            throws RestaurantNotFoundException {
        log.info("Fetching restaurant for user ID: {}", userId);

        Restaurant restaurant = restaurantRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found for user ID: {}", userId);
                    return new RestaurantNotFoundException(
                            "Restaurant not found for user ID: " + userId);
                });

        log.info("Restaurant found: {}", restaurant.getRestaurantName());
        return convertToDto(restaurant);
    }

    /**
     * Fetches single restaurant by ID and converts to DTO.
     *
     * @param restaurantId the ID of the restaurant
     * @return RestaurantResponseDto with restaurant details
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public RestaurantResponseDto getRestaurant(Long restaurantId)
            throws RestaurantNotFoundException {
        log.info("Fetching restaurant with ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found with ID: {}", restaurantId);
                    return new RestaurantNotFoundException(
                            "Restaurant not found with ID: " + restaurantId);
                });

        log.info("Restaurant found: {}", restaurant.getRestaurantName());
        return convertToDto(restaurant);
    }

    /**
     * Fetches all restaurants and converts each to DTO.
     *
     * @return list of all RestaurantResponseDto
     */
    @Override
    public List<RestaurantResponseDto> getAllRestaurants() {
        log.info("Fetching all restaurants");

        List<RestaurantResponseDto> list = restaurantRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.info("Found {} restaurants", list.size());
        return list;
    }

    /**
     * Validates ID, fetches restaurant and updates with new details.
     *
     * @param restaurantId the ID of restaurant to update
     * @param dto          contains new name, location and contact
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto dto)
            throws InvalidRequestException, RestaurantNotFoundException {
        log.info("Updating restaurant with ID: {}", restaurantId);

        if (restaurantId == null || restaurantId <= 0) {
            log.warn("Invalid restaurant ID: {}", restaurantId);
            throw new InvalidRequestException("Invalid restaurant ID.");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found with ID: {}", restaurantId);
                    return new RestaurantNotFoundException(
                            "Restaurant not found with ID: " + restaurantId);
                });

        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully with ID: {}", restaurantId);
    }

    /**
     * Partially updates restaurant with only provided fields.
     * Null fields are ignored — not updated.
     *
     * @param restaurantId the ID of restaurant
     * @param patchDto     contains only fields to update
     * @throws InvalidRequestException     if ID invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public void patchRestaurant(Long restaurantId, RestaurantPatchDto patchDto)
            throws InvalidRequestException, RestaurantNotFoundException {
        log.info("PATCH restaurant with ID: {}", restaurantId);

        if (restaurantId == null || restaurantId <= 0) {
            log.warn("Invalid restaurant ID: {}", restaurantId);
            throw new InvalidRequestException("Invalid restaurant ID.");
        }

        Restaurant restaurant = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> {
                    log.error("Restaurant not found with ID: {}", restaurantId);
                    return new RestaurantNotFoundException(
                            "Restaurant not found with ID: " + restaurantId);
                });

        if (patchDto.getRestaurantName() != null &&
                !patchDto.getRestaurantName().trim().isEmpty()) {
            restaurant.setRestaurantName(patchDto.getRestaurantName());
        }
        if (patchDto.getLocation() != null &&
                !patchDto.getLocation().trim().isEmpty()) {
            restaurant.setLocation(patchDto.getLocation());
        }
        if (patchDto.getContactNumber() != null &&
                !patchDto.getContactNumber().trim().isEmpty()) {
            restaurant.setContactNumber(patchDto.getContactNumber());
        }

        restaurantRepository.save(restaurant);
        log.info("Restaurant patched successfully with ID: {}", restaurantId);
    }

    /**
     * Deletes restaurant — CascadeType.ALL deletes menu items automatically.
     *
     * @param restaurantId the ID of restaurant to delete
     * @throws InvalidRequestException     if ID is invalid
     * @throws RestaurantNotFoundException if restaurant not found
     */
    @Override
    public void deleteRestaurant(Long restaurantId)
            throws InvalidRequestException, RestaurantNotFoundException {
        log.info("Deleting restaurant with ID: {}", restaurantId);

        if (restaurantId == null || restaurantId <= 0) {
            log.warn("Invalid restaurant ID: {}", restaurantId);
            throw new InvalidRequestException("Invalid restaurant ID.");
        }
        if (!restaurantRepository.existsById(restaurantId)) {
            log.error("Restaurant not found with ID: {}", restaurantId);
            throw new RestaurantNotFoundException(
                    "Restaurant not found with ID: " + restaurantId);
        }

        restaurantRepository.deleteById(restaurantId);
        log.info("Restaurant deleted successfully with ID: {}", restaurantId);
    }

    /**
     * Converts Restaurant entity to RestaurantResponseDto.
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