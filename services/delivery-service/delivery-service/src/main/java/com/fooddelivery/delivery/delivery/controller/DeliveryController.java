package com.fooddelivery.delivery.delivery.controller;

import com.fooddelivery.delivery.delivery.dto.*;
import com.fooddelivery.delivery.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // DeliveryController.java — extract and pass token
    @PostMapping("/assign/{orderId}")
    public ResponseEntity<DeliveryResponse> assignAgent(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                deliveryService.assignAgent(orderId, token));
    }

    @PostMapping("/food-ready/{orderId}")
    public ResponseEntity<DeliveryResponse> foodReady(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                deliveryService.assignAgent(orderId, token));
    }

    // ← removed @PreAuthorize
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long deliveryId,
            @RequestBody DeliveryStatusRequest request) {
        return ResponseEntity.ok(
                deliveryService.updateStatus(
                        deliveryId, request.getStatus()));
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getDelivery(
            @PathVariable Long deliveryId) {
        return ResponseEntity.ok(
                deliveryService.getDeliveryById(deliveryId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> trackDelivery(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(
                deliveryService.getDeliveryByOrderId(orderId));
    }

    @GetMapping("/agent/{agentId}/current")
    public ResponseEntity<DeliveryResponse> getAgentCurrent(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                deliveryService.getAgentCurrentDelivery(agentId));
    }

    @GetMapping("/agent/{agentId}/history")
    public ResponseEntity<List<DeliveryResponse>> getHistory(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                deliveryService.getAgentDeliveryHistory(agentId));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelDelivery(
            @PathVariable Long orderId) {
        deliveryService.cancelDelivery(orderId);
        return ResponseEntity.ok().build();
    }

}