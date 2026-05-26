package com.fooddelivery.delivery.delivery.service;

import com.fooddelivery.delivery.agent.model.Agent;
import com.fooddelivery.delivery.agent.repository.AgentRepository;
import com.fooddelivery.delivery.delivery.dto.*;
import com.fooddelivery.delivery.delivery.model.Delivery;
import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import com.fooddelivery.delivery.delivery.repository.DeliveryRepository;
import com.fooddelivery.delivery.exception.AgentNotFoundException;
import com.fooddelivery.delivery.exception.DeliveryNotFoundException;
import com.fooddelivery.delivery.exception.NoAgentAvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final AgentRepository agentRepository;

    @Override
    public DeliveryResponse assignAgent(Long orderId) {

        // 1. find nearest available agent
        Agent agent = agentRepository
                .findFirstByIsAvailableTrue()
                .orElseThrow(() -> new NoAgentAvailableException(
                        "No agents available right now"));

        // 2. create delivery record
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .agentId(agent.getAgentId())
                .status(DeliveryStatus.AGENT_ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .estimatedDeliveryTime(
                        LocalDateTime.now().plusMinutes(30))
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        // 3. mark agent as unavailable
        agent.setAvailable(false);
        agent.setCurrentDeliveryId(saved.getDeliveryId());
        agentRepository.save(agent);

        return mapToResponse(saved);
    }

    @Override
    public DeliveryResponse updateStatus(Long deliveryId,
                                         DeliveryStatus status) {

        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setStatus(status);

        switch (status) {
            case PICKED_UP ->
                    delivery.setPickedUpAt(LocalDateTime.now());
            case DELIVERED -> {
                delivery.setDeliveredAt(LocalDateTime.now());
                freeAgent(delivery.getAgentId());
            }
            case CANCELLED -> {
                delivery.setCancelledAt(LocalDateTime.now());
                freeAgent(delivery.getAgentId());
            }
            default -> {}
        }

        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryResponse getDeliveryById(Long deliveryId) {
        return mapToResponse(findDeliveryById(deliveryId));
    }

    @Override
    public DeliveryResponse getDeliveryByOrderId(
            Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order: " + orderId));
    }

    @Override
    public DeliveryResponse getAgentCurrentDelivery(
            Long agentId) {
        return deliveryRepository
                .findByAgentIdAndStatus(agentId,
                        DeliveryStatus.AGENT_ASSIGNED)
                .map(this::mapToResponse)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "No active delivery for agent: " + agentId));
    }

    @Override
    public List<DeliveryResponse> getAgentDeliveryHistory(
            Long agentId) {
        return deliveryRepository.findByAgentId(agentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelDelivery(Long orderId) {
        Delivery delivery = deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order: " + orderId));
        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setCancelledAt(LocalDateTime.now());
        freeAgent(delivery.getAgentId());
        deliveryRepository.save(delivery);
    }

    // ── helpers ──────────────────────────────────────

    private void freeAgent(Long agentId) {
        agentRepository.findById(agentId).ifPresent(agent -> {
            agent.setAvailable(true);
            agent.setCurrentDeliveryId(null);
            agent.setTotalDeliveries(
                    agent.getTotalDeliveries() + 1);
            agentRepository.save(agent);
        });
    }

    private Delivery findDeliveryById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found: " + deliveryId));
    }

    private DeliveryResponse mapToResponse(Delivery d) {
        return DeliveryResponse.builder()
                .deliveryId(d.getDeliveryId())
                .orderId(d.getOrderId())
                .agentId(d.getAgentId())
                .status(d.getStatus())
                .deliveryAddress(d.getDeliveryAddress())
                .assignedAt(d.getAssignedAt())
                .pickedUpAt(d.getPickedUpAt())
                .deliveredAt(d.getDeliveredAt())
                .estimatedDeliveryTime(
                        d.getEstimatedDeliveryTime())
                .build();
    }
}