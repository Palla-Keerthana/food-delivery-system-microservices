package com.fooddelivery.delivery.agent.repository;

import com.fooddelivery.delivery.agent.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository
        extends JpaRepository<Agent, Long> {

    // find agent by userId from auth-service
    Optional<Agent> findByUserId(Long userId);

    // find first available agent
    Optional<Agent> findFirstByIsAvailableTrue();

    // find all available agents
    List<Agent> findByIsAvailableTrue();

    // find agent by current delivery
    Optional<Agent> findByCurrentDeliveryId(
            Long deliveryId);

    // check if agent exists by userId
    boolean existsByUserId(Long userId);


}