package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
import com.fooddelivery.orderservice.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {

    // Place a new order — takes OrderRequestDto, returns OrderResponseDto
    OrderResponseDto placeOrder(OrderRequestDto request);

    // Get a single order by its ID
    OrderResponseDto getOrderById(Long orderId);

    // Get all orders placed by a specific customer
    List<OrderResponseDto> getOrdersByCustomer(Long customerId);

    // Update the status of an existing order (PLACED → PREPARING etc.)
    void updateOrderStatus(Long orderId, String status);

    // Get only the status string of an order
    String getOrderStatus(Long orderId);
}