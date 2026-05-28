package com.fooddelivery.orderservice.controller;

import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
import com.fooddelivery.orderservice.dto.response.OrderResponseDto;
import com.fooddelivery.orderservice.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @Valid @RequestBody OrderRequestDto request) {
        log.info("Place order request received for customerId: {}", request.getCustomerId());
        OrderResponseDto response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId) {
        log.info("Get order request received for orderId: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(
            @PathVariable @Min(value = 1, message = "Customer ID must be greater than 0") Long customerId) {
        log.info("Get orders request received for customerId: {}", customerId);
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // PUT /api/orders/{orderId}/status?status=PREPARING
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId,
            @RequestParam @NotBlank(message = "Status cannot be empty") String status) {
        log.info("Update status request received for orderId: {}, status: {}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/{orderId}/status
    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(
            @PathVariable @Min(value = 1, message = "Order ID must be greater than 0") Long orderId) {
        log.info("Get status request received for orderId: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }

    // GET /api/orders/restaurant/{restaurantId}
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByRestaurant(
            @PathVariable @Min(value = 1, message = "Restaurant ID must be greater than 0") Long restaurantId) {
        log.info("Get orders request received for restaurantId: {}", restaurantId);
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }
}