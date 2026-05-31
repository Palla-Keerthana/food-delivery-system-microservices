package com.fooddelivery.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "order-service", url="http://localhost:8084")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrder(
            @PathVariable Long orderId);

    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> status);

}