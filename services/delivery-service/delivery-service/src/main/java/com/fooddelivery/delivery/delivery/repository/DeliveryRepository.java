package com.fooddelivery.delivery.delivery.repository;

import com.fooddelivery.delivery.delivery.model.Delivery;
import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByAgentId(Long agentId);

    Optional<Delivery> findByAgentIdAndStatus(
            Long agentId, DeliveryStatus status);

    List<Delivery> findByStatus(DeliveryStatus status);
}