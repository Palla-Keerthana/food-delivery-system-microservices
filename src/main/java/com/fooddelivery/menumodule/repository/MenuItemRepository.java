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

    @Transactional
    void deleteByRestaurantId(Long restaurantId);

    List<MenuItem> findByRestaurantId(Long restaurantId);


    List<MenuItem> findByAvailableTrueAndQuantityGreaterThan(int quantity);

    List<MenuItem> findByRestaurantIdAndAvailableTrueAndQuantityGreaterThan(
            Long restaurantId, int quantity);

    MenuItem findByNameAndRestaurantId(String name, Long restaurantId);

    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.quantity = m.quantity - :qty, " +
            "m.available = CASE WHEN m.quantity - :qty <= 0 THEN false ELSE true END " +
            "WHERE m.itemId = :itemId AND m.quantity >= :qty")
    void reduceQuantity(@Param("itemId") Long itemId, @Param("qty") int qty);
}