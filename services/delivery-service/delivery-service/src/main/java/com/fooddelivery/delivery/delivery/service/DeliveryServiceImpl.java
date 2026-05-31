package com.fooddelivery.delivery.delivery.service;

import com.fooddelivery.delivery.agent.model.Agent;
import com.fooddelivery.delivery.agent.repository.AgentRepository;
import com.fooddelivery.delivery.client.OrderResponse;
import com.fooddelivery.delivery.client.OrderServiceClient;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final AgentRepository agentRepository;
    private final OrderServiceClient orderServiceClient;
    @Override
    public DeliveryResponse assignAgent(Long orderId) {
        OrderResponse order;
        try {
            order = orderServiceClient.getOrder(orderId);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not fetch order details: "
                            + e.getMessage());
        }

        // Step 2 — validate order status
        if (!order.getStatus().equals("CONFIRMED") &&
                !order.getStatus().equals("PREPARING") &&
                !order.getStatus().equals("READY")) {
            throw new RuntimeException(
                    "Order not ready for delivery. "
                            + "Current status: "
                            + order.getStatus());
        }
        // 1. find nearest available agent
        Agent agent = agentRepository
                .findFirstByIsAvailableTrue()
                .orElseThrow(() -> new NoAgentAvailableException(
                        "No agents available right now"));

        // 2. create delivery record
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .agentId(agent.getAgentId())
                .deliveryAddress(order.getCustomerAddress())
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

            case ON_THE_WAY -> {
                // agent picked up and on the way
                // estimate 10 mins from now
                delivery.setEstimatedDeliveryTime(
                        LocalDateTime.now().plusMinutes(10));
            }
            case DELIVERED -> {
                delivery.setDeliveredAt(LocalDateTime.now());
                freeAgent(delivery.getAgentId());

                // notify order-service → COMPLETED
                try {
                    orderServiceClient.updateOrderStatus(
                            delivery.getOrderId(),
                            Map.of("status", "COMPLETED"));
                } catch (Exception e) {
                    System.out.println(
                            "Could not update order status: "
                                    + e.getMessage());
                }
            }

            case CANCELLED -> {
                delivery.setCancelledAt(LocalDateTime.now());
                freeAgent(delivery.getAgentId());
                // notify order-service → CANCELLED
                try {
                    orderServiceClient.updateOrderStatus(
                            delivery.getOrderId(),
                            Map.of("status", "CANCELLED"));
                } catch (Exception e) {
                    System.out.println(
                            "Could not update order status: "
                                    + e.getMessage());
                }
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
        Delivery delivery = deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order: " + orderId));

        // get agent details from agents table
        String agentName = null;
        String agentPhone = null;
        try {
            Agent agent = agentRepository
                    .findById(delivery.getAgentId())
                    .orElse(null);
            if (agent != null) {
                agentName = agent.getName();
                agentPhone = agent.getPhone();
            }
        } catch (Exception e) {
            System.out.println(
                    "Could not fetch agent: "
                            + e.getMessage());
        }

        return DeliveryResponse.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .agentId(delivery.getAgentId())
                .agentName(agentName)
                .agentPhone(agentPhone)
                .status(delivery.getStatus())
                .deliveryAddress(delivery.getDeliveryAddress())
                .assignedAt(delivery.getAssignedAt())
                .pickedUpAt(delivery.getPickedUpAt())
                .deliveredAt(delivery.getDeliveredAt())
                .estimatedDeliveryTime(
                        delivery.getEstimatedDeliveryTime())
                .build();
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
        // notify order-service
        try {
            orderServiceClient.updateOrderStatus(
                    orderId,
                    Map.of("status", "CANCELLED"));
        } catch (Exception e) {
            System.out.println(
                    "Could not update order: "
                            + e.getMessage());
        }
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