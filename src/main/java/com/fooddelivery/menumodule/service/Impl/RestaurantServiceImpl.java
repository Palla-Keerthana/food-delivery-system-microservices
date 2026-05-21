package com.fooddelivery.menumodule.service.Impl;

import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.MenuItemNotFoundException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository; // ← ADD THIS!

    @Override
    public void registerRestaurant(RestaurantRequestDto dto) {
        // ✅ validate input
        if (dto.getRestaurantName() == null || dto.getRestaurantName().trim().isEmpty()) {
            throw new InvalidRequestException("Restaurant name cannot be empty.");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new InvalidRequestException("Location cannot be empty.");
        }
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            throw new InvalidRequestException("Contact number cannot be empty.");
        }

        // ✅ convert DTO to Entity
        Restaurant restaurant = new Restaurant();
        restaurant.setUserId(dto.getUserId());
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        restaurantRepository.save(restaurant);
    }

    @Override
    public RestaurantResponseDto getByUserId(Long userId) {
        Restaurant restaurant = restaurantRepository.findByUserId(userId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found for user ID: " + userId));
        return convertToDto(restaurant);
    }

    @Override
    public RestaurantResponseDto getRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with ID: " + restaurantId));
        return convertToDto(restaurant);
    }

    @Override
    public List<RestaurantResponseDto> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRestaurant(Long restaurantId, RestaurantRequestDto dto) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidRequestException("Invalid restaurant ID.");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with ID: " + restaurantId));

        // ✅ only update these 3 fields — NOT userId!
        restaurant.setRestaurantName(dto.getRestaurantName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setContactNumber(dto.getContactNumber());
        // ✅ userId stays same — never changes!

        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional // ← important! both deletes in one transaction
    public void deleteRestaurant(Long restaurantId) {
        // ✅ validate
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidRequestException("Invalid restaurant ID.");
        }
        // ✅ check if exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(
                    "Restaurant not found with ID: " + restaurantId);
        }

        // ✅ Step 1 — delete menu items first!
        menuItemRepository.deleteByRestaurantId(restaurantId);

        // ✅ Step 2 — then delete restaurant
        restaurantRepository.deleteById(restaurantId);
    }

    // ✅ private helper
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