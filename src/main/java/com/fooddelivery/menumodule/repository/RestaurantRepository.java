package com.fooddelivery.menumodule.repository;

import com.fooddelivery.menumodule.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
//                                              ↑           ↑
//
    // ✅ Find restaurant by userId
    Optional<Restaurant> findByUserId(Long userId);

    // ✅ Find restaurant by name
    Optional<Restaurant> findByRestaurantName(String restaurantName);

    // ✅ Find restaurants by location
    List<Restaurant> findByLocation(String location);
}