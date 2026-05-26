package com.fooddelivery.delivery.agent.service;

import com.fooddelivery.delivery.agent.dto.*;
import com.fooddelivery.delivery.delivery.dto.DeliveryResponse;
import java.time.LocalDate;
import java.util.List;

public interface AgentService {

    AgentResponse registerAgent(Long userId, String name, String phone);

    AgentResponse getAgentStatus(Long agentId);

    AgentResponse updateAgentDetails(Long agentId,
                                     AgentUpdateRequest request);

    void updateAvailability(Long agentId, boolean isAvailable);

    void updateLocation(Long agentId, Double latitude,
                        Double longitude);

    DeliveryResponse getCurrentDelivery(Long agentId);

    List<DeliveryResponse> getDeliveryHistory(Long agentId);

    EarningsResponse getEarnings(Long agentId);

    EarningsResponse getEarningsByRange(Long agentId,
                                        LocalDate from,
                                        LocalDate to);

    RatingResponse getRatings(Long agentId);
}