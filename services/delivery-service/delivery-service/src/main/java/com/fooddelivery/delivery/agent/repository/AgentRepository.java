package com.fooddelivery.delivery.agent.repository;

import com.fooddelivery.delivery.agent.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    // find first available agent
    Optional<Agent> findFirstByIsAvailableTrue();

    // find all available agents
    List<Agent> findByIsAvailableTrue();

    // find agent by current delivery
    Optional<Agent> findByCurrentDeliveryId(Long deliveryId);

    // find nearest available agent by location
    @Query(value = """
        SELECT * FROM agents a
        WHERE a.is_available = true
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(a.current_latitude)) *
                cos(radians(a.current_longitude) - radians(:lon)) +
                sin(radians(:lat)) * sin(radians(a.current_latitude))
            )
        ) ASC LIMIT 1
        """, nativeQuery = true)
    Optional<Agent> findNearestAvailableAgent(Double lat, Double lon);
}