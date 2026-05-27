package com.fooddelivery.orderservice.controller;

import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
import com.fooddelivery.orderservice.dto.response.OrderResponseDto;
import com.fooddelivery.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders
    // Request body: OrderRequestDto  →  Response body: OrderResponseDto
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @RequestBody OrderRequestDto request) {

        OrderResponseDto response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders/{orderId}
    // Response body: OrderResponseDto
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/customer/{customerId}
    // Response body: List<OrderResponseDto>
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // PUT /api/orders/{orderId}/status?status=PREPARING
    // Response body: updated OrderResponseDto
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // GET /api/orders/{orderId}/status
    // Response body: plain status string
    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }
}