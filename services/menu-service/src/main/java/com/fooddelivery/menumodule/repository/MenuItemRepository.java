package com.fooddelivery.menumodule.repository;

import com.fooddelivery.menumodule.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Deletes all menu items belonging to a specific restaurant.
     * Called before deleting restaurant to prevent foreign key errors.
     * CascadeType.ALL handles this automatically now!
     */
    @Transactional
    void deleteByRestaurant_RestaurantId(Long restaurantId);

    /**
     * Finds all menu items for a specific restaurant.
     * Uses restaurant_restaurantId pattern for JPA relationship.
     */
    List<MenuItem> findByRestaurant_RestaurantId(Long restaurantId);

    /**
     * Finds all available items with stock greater than zero.
     * Used by customers to view all available items.
     */
    List<MenuItem> findByAvailableTrueAndQuantityGreaterThan(int quantity);

    /**
     * Finds available items with stock for a specific restaurant.
     * Used when customer selects restaurant to order from.
     */
    List<MenuItem> findByRestaurant_RestaurantIdAndAvailableTrueAndQuantityGreaterThan(
            Long restaurantId, int quantity);

    /**
     * Finds menu item by name within a specific restaurant.
     * Used during order placement when customer types item name.
     */
    MenuItem findByNameAndRestaurant_RestaurantId(
            String name, Long restaurantId);

    /**
     * Reduces stock quantity after order placed.
     * Automatically marks item unavailable if stock reaches zero.
     */
    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.quantity = m.quantity - :qty, " +
            "m.available = CASE WHEN m.quantity - :qty <= 0 THEN false ELSE true END " +
            "WHERE m.itemId = :itemId AND m.quantity >= :qty")
    void reduceQuantity(@Param("itemId") Long itemId, @Param("qty") int qty);
}