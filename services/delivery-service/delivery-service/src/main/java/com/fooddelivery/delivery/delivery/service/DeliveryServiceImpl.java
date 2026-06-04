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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final AgentRepository agentRepository;
    private final OrderServiceClient orderServiceClient;

    @Override
    public DeliveryResponse assignAgent(
            Long orderId, String token) {

        // Step 1 — fetch order details
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
                !order.getStatus().equals("PAYMENT_PENDING") &&
                !order.getStatus().equals("READY")) {
            throw new RuntimeException(
                    "Order not ready for delivery. "
                            + "Current status: "
                            + order.getStatus());
        }

        // Step 3 — find available agent
        Agent agent = agentRepository
                .findFirstByIsAvailableTrue()
                .orElseThrow(() -> new NoAgentAvailableException(
                        "No agents available right now"));

        // Step 4 — create delivery record
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .agent(agent)
                .deliveryAddress(order.getCustomerAddress())
                .status(DeliveryStatus.AGENT_ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .estimatedDeliveryTime(
                        LocalDateTime.now().plusMinutes(3))
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        // Step 5 — mark agent busy
        agent.setAvailable(false);
        agent.setCurrentDeliveryId(saved.getDeliveryId());
        agentRepository.save(agent);

        // Step 6 — start auto simulation
        simulateDeliveryFlow(
                saved.getDeliveryId(), token);

        return mapToResponse(saved);
    }

    @Async
    public void simulateDeliveryFlow(
            Long deliveryId, String token) {
        try {
            // wait 1 min → PICKED_UP
            TimeUnit.MINUTES.sleep(1);
            Delivery d = deliveryRepository
                    .findById(deliveryId).orElse(null);
            if (d == null) return;
            d.setStatus(DeliveryStatus.PICKED_UP);
            d.setPickedUpAt(LocalDateTime.now());
            deliveryRepository.save(d);
            System.out.println(
                    "✅ Delivery " + deliveryId
                            + " → PICKED_UP");

            // wait 1 min → ON_THE_WAY
            TimeUnit.MINUTES.sleep(1);
            d.setStatus(DeliveryStatus.ON_THE_WAY);
            d.setEstimatedDeliveryTime(
                    LocalDateTime.now().plusMinutes(1));
            deliveryRepository.save(d);
            System.out.println(
                    "✅ Delivery " + deliveryId
                            + " → ON_THE_WAY");

            // wait 1 min → DELIVERED
            TimeUnit.MINUTES.sleep(1);
            d.setStatus(DeliveryStatus.DELIVERED);
            d.setDeliveredAt(LocalDateTime.now());
            deliveryRepository.save(d);
            freeAgent(d.getAgentId());
            System.out.println(
                    "✅ Delivery " + deliveryId
                            + " → DELIVERED");

            // update order → COMPLETED
            try {
                orderServiceClient
                        .updateOrderStatusWithToken(
                                d.getOrderId(),
                                "COMPLETED",
                                token);
                System.out.println(
                        "✅ Order " + d.getOrderId()
                                + " → COMPLETED");
            } catch (Exception e) {
                System.out.println(
                        "❌ Order update failed: "
                                + e.getMessage());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public DeliveryResponse updateStatus(
            Long deliveryId,
            DeliveryStatus status) {

        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setStatus(status);

        switch (status) {
            case PICKED_UP ->
                    delivery.setPickedUpAt(
                            LocalDateTime.now());

            case ON_THE_WAY ->
                    delivery.setEstimatedDeliveryTime(
                            LocalDateTime.now().plusMinutes(10));

            case DELIVERED -> {
                delivery.setDeliveredAt(
                        LocalDateTime.now());
                freeAgent(delivery.getAgentId());

                try {
                    orderServiceClient.updateOrderStatus(
                            delivery.getOrderId(),
                            "COMPLETED");
                } catch (Exception e) {
                    System.out.println(
                            "Could not update order: "
                                    + e.getMessage());
                }
            }

            case CANCELLED -> {
                delivery.setCancelledAt(
                        LocalDateTime.now());
                freeAgent(delivery.getAgentId());

                try {
                    orderServiceClient.updateOrderStatus(
                            delivery.getOrderId(),
                            "CANCELLED");
                } catch (Exception e) {
                    System.out.println(
                            "Could not update order: "
                                    + e.getMessage());
                }
            }
            default -> {}
        }

        return mapToResponse(
                deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryResponse getDeliveryById(
            Long deliveryId) {
        return mapToResponse(
                findDeliveryById(deliveryId));
    }

    @Override
    public DeliveryResponse getDeliveryByOrderId(
            Long orderId) {

        Delivery delivery = deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order: "
                                + orderId));

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
                        "No active delivery for agent: "
                                + agentId));
    }

    @Override
    public List<DeliveryResponse> getAgentDeliveryHistory(
            Long agentId) {
        return deliveryRepository
                .findByAgentId(agentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelDelivery(Long orderId) {
        Delivery delivery = deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found for order: "
                                + orderId));

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setCancelledAt(LocalDateTime.now());
        freeAgent(delivery.getAgentId());
        deliveryRepository.save(delivery);

        try {
            orderServiceClient.updateOrderStatus(
                    orderId, "CANCELLED");
        } catch (Exception e) {
            System.out.println(
                    "Could not update order: "
                            + e.getMessage());
        }
    }

    // ── helpers ──────────────────────────────────────

    private void freeAgent(Long agentId) {
        agentRepository.findById(agentId)
                .ifPresent(agent -> {
                    agent.setAvailable(true);
                    agent.setCurrentDeliveryId(null);
                    agent.setTotalDeliveries(
                            agent.getTotalDeliveries() + 1);

                    double earningPerDelivery = 50.0;
                    agent.setTotalEarnings(
                            agent.getTotalEarnings()
                                    + earningPerDelivery);


                    agentRepository.save(agent);
                });
    }

    private Delivery findDeliveryById(Long deliveryId) {
        return deliveryRepository
                .findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found: " + deliveryId));
    }

    private DeliveryResponse mapToResponse(Delivery d) {
        String agentName = null;
        String agentPhone = null;

        // first try from relationship
        if (d.getAgent() != null) {
            agentName = d.getAgent().getName();
            agentPhone = d.getAgent().getPhone();
        } else if (d.getAgentId() != null) {
            // fallback to repository
            try {
                Agent agent = agentRepository
                        .findById(d.getAgentId())
                        .orElse(null);
                if (agent != null) {
                    agentName = agent.getName();
                    agentPhone = agent.getPhone();
                }
            } catch (Exception e) {
                System.out.println(
                        "Agent fetch failed: "
                                + e.getMessage());
            }
        }

        return DeliveryResponse.builder()
                .deliveryId(d.getDeliveryId())
                .orderId(d.getOrderId())
                .agentId(d.getAgentId())
                .agentName(agentName)
                .agentPhone(agentPhone)
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