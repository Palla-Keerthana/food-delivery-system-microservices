package com.fooddelivery.delivery.delivery.service;

import com.fooddelivery.delivery.delivery.dto.*;
import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import java.util.List;

public interface DeliveryService {

    DeliveryResponse assignAgent(Long orderId);

    DeliveryResponse updateStatus(Long deliveryId,
                                  DeliveryStatus status);

    DeliveryResponse getDeliveryById(Long deliveryId);

    DeliveryResponse getDeliveryByOrderId(Long orderId);

    DeliveryResponse getAgentCurrentDelivery(Long agentId);

    List<DeliveryResponse> getAgentDeliveryHistory(
            Long agentId);

    void cancelDelivery(Long orderId);
}