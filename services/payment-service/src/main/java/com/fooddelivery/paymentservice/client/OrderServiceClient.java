package com.fooddelivery.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "order-service",
        url = "http://localhost:8084")  // remove when Eureka ready
public interface OrderServiceClient {

    // get order details → fetch amount
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrder(
            @PathVariable Long orderId);

    // update order status after payment
    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam("status") String status);
}