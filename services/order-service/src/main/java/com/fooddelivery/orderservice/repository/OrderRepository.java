package com.fooddelivery.orderservice.repository;

import com.fooddelivery.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // replaces: findByCustomerId SQL query
    List<Order> findByCustomerId(Long customerId);

    // replaces: findByRestaurantId SQL query
    List<Order> findByRestaurantId(Long restaurantId);
}