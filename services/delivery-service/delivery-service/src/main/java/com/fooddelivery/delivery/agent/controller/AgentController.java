package com.fooddelivery.delivery.agent.controller;

import com.fooddelivery.delivery.agent.dto.*;
import com.fooddelivery.delivery.agent.service.AgentService;
import com.fooddelivery.delivery.delivery.dto.DeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    // get agent profile
    @GetMapping("/{agentId}")
    public ResponseEntity<AgentResponse> getAgent(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentService.getAgentStatus(agentId));
    }

    // update agent details
    @PutMapping("/{agentId}")
    public ResponseEntity<AgentResponse> updateAgent(
            @PathVariable Long agentId,
            @RequestBody AgentUpdateRequest request) {
        return ResponseEntity.ok(
                agentService.updateAgentDetails(agentId, request));
    }

    // go online or offline
    @PutMapping("/{agentId}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long agentId,
            @RequestBody AgentStatusRequest request) {
        agentService.updateAvailability(
                agentId, request.isAvailable());
        return ResponseEntity.ok().build();
    }

    // update GPS location
    @PutMapping("/{agentId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long agentId,
            @RequestBody AgentLocationRequest request) {
        agentService.updateLocation(
                agentId,
                request.getLatitude(),
                request.getLongitude());
        return ResponseEntity.ok().build();
    }

    // get current active delivery
    @GetMapping("/{agentId}/current-delivery")
    public ResponseEntity<DeliveryResponse> getCurrentDelivery(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentService.getCurrentDelivery(agentId));
    }

    // get all past deliveries
    @GetMapping("/{agentId}/deliveries")
    public ResponseEntity<List<DeliveryResponse>> getHistory(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentService.getDeliveryHistory(agentId));
    }

    // get earnings summary
    @GetMapping("/{agentId}/earnings")
    public ResponseEntity<EarningsResponse> getEarnings(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentService.getEarnings(agentId));
    }

    // get earnings by date range
    @GetMapping("/{agentId}/earnings/range")
    public ResponseEntity<EarningsResponse> getEarningsByRange(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(
                agentService.getEarningsByRange(agentId, from, to));
    }

    // get ratings
    @GetMapping("/{agentId}/ratings")
    public ResponseEntity<RatingResponse> getRatings(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentService.getRatings(agentId));
    }
}