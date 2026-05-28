package com.fooddelivery.orderservice.controller;

import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
import com.fooddelivery.orderservice.dto.response.OrderResponseDto;
import com.fooddelivery.orderservice.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @Valid @RequestBody OrderRequestDto request) {

        OrderResponseDto response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId) {

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(
            @PathVariable @Min(value = 1, message = "Customer ID must be greater than 0") Long customerId) {

        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // PUT /api/orders/{orderId}/status?status=PREPARING
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId,
            @RequestParam @jakarta.validation.constraints.NotBlank(message = "Status cannot be empty") String status) {

        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/{orderId}/status
    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId) {

        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }


    // GET /api/orders/restaurant/{restaurantId}
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByRestaurant(
            @PathVariable @Min(value = 1, message = "Restaurant ID must be greater than 0") Long restaurantId) {

        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }
}