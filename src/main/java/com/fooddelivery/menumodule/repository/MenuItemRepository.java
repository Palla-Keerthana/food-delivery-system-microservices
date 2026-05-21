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
//                                           ↑         ↑
//                                        Entity    ID type
    @Transactional
    void deleteByRestaurantId(Long restaurantId);
    // ✅ Find all items by restaurant
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // ✅ Find all available items with stock > 0
    List<MenuItem> findByAvailableTrueAndQuantityGreaterThan(int quantity);

    // ✅ Find available items by restaurant
    List<MenuItem> findByRestaurantIdAndAvailableTrueAndQuantityGreaterThan(
            Long restaurantId, int quantity);

    // ✅ Find item by name and restaurant
    MenuItem findByNameAndRestaurantId(String name, Long restaurantId);

    // ✅ Reduce quantity after order placed
    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.quantity = m.quantity - :qty, " +
            "m.available = CASE WHEN m.quantity - :qty <= 0 THEN false ELSE true END " +
            "WHERE m.itemId = :itemId AND m.quantity >= :qty")
    void reduceQuantity(@Param("itemId") Long itemId, @Param("qty") int qty);
}