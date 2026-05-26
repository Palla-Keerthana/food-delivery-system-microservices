package com.fooddelivery.delivery.agent.service;

import com.fooddelivery.delivery.agent.dto.*;
import com.fooddelivery.delivery.agent.model.Agent;
import com.fooddelivery.delivery.agent.repository.AgentRepository;
import com.fooddelivery.delivery.delivery.dto.DeliveryResponse;
import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import com.fooddelivery.delivery.delivery.repository.DeliveryRepository;
import com.fooddelivery.delivery.exception.AgentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public AgentResponse registerAgent(Long userId,
                                       String name,
                                       String phone) {
        Agent agent = Agent.builder()
                .agentId(userId)
                .name(name)
                .phone(phone)
                .isAvailable(false)
                .build();
        return mapToResponse(agentRepository.save(agent));
    }

    @Override
    public AgentResponse getAgentStatus(Long agentId) {
        Agent agent = findAgentById(agentId);
        return mapToResponse(agent);
    }

    @Override
    public AgentResponse updateAgentDetails(Long agentId,
                                            AgentUpdateRequest req) {
        Agent agent = findAgentById(agentId);
        if (req.getPhone() != null)
            agent.setPhone(req.getPhone());

        if (req.getProfilePhoto() != null)
            agent.setProfilePhoto(req.getProfilePhoto());
        return mapToResponse(agentRepository.save(agent));
    }

    @Override
    public void updateAvailability(Long agentId,
                                   boolean isAvailable) {
        Agent agent = findAgentById(agentId);
        agent.setAvailable(isAvailable);
        agentRepository.save(agent);
    }

    @Override
    public void updateLocation(Long agentId,
                               Double latitude,
                               Double longitude) {
        Agent agent = findAgentById(agentId);
        agent.setCurrentLatitude(latitude);
        agent.setCurrentLongitude(longitude);
        agent.setLocationUpdatedAt(
                java.time.LocalDateTime.now());
        agentRepository.save(agent);
    }

    @Override
    public DeliveryResponse getCurrentDelivery(Long agentId) {
        Agent agent = findAgentById(agentId);
        return deliveryRepository
                .findByAgentIdAndStatus(agentId,
                        DeliveryStatus.AGENT_ASSIGNED)
                .map(this::mapDeliveryToResponse)
                .orElse(null);
    }

    @Override
    public List<DeliveryResponse> getDeliveryHistory(
            Long agentId) {
        return deliveryRepository.findByAgentId(agentId)
                .stream()
                .map(this::mapDeliveryToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EarningsResponse getEarnings(Long agentId) {
        Agent agent = findAgentById(agentId);
        return EarningsResponse.builder()
                .agentId(agentId)
                .totalEarnings(agent.getTotalEarnings())
                .totalDeliveries(agent.getTotalDeliveries())
                .build();
    }

    @Override
    public EarningsResponse getEarningsByRange(Long agentId,
                                               LocalDate from,
                                               LocalDate to) {
        // implement date range earnings logic later
        return getEarnings(agentId);
    }

    @Override
    public RatingResponse getRatings(Long agentId) {
        Agent agent = findAgentById(agentId);
        return RatingResponse.builder()
                .agentId(agentId)
                .averageRating(agent.getRating())
                .totalRatings(agent.getTotalRatings())
                .build();
    }

    // ── helpers ──────────────────────────────────────

    private Agent findAgentById(Long agentId) {
        return agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(
                        "Agent not found with id: " + agentId));
    }

    private AgentResponse mapToResponse(Agent agent) {
        return AgentResponse.builder()
                .agentId(agent.getAgentId())
                .name(agent.getName())
                .phone(agent.getPhone())
                .isAvailable(agent.isAvailable())
                .currentLatitude(agent.getCurrentLatitude())
                .currentLongitude(agent.getCurrentLongitude())
                .totalDeliveries(agent.getTotalDeliveries())
                .rating(agent.getRating())
                .totalEarnings(agent.getTotalEarnings())
                .build();
    }

    private DeliveryResponse mapDeliveryToResponse(
            com.fooddelivery.delivery.delivery.model
                    .Delivery delivery) {
        return DeliveryResponse.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .agentId(delivery.getAgentId())
                .status(delivery.getStatus())
                .assignedAt(delivery.getAssignedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .estimatedDeliveryTime(
                        delivery.getEstimatedDeliveryTime())
                .build();
    }
}