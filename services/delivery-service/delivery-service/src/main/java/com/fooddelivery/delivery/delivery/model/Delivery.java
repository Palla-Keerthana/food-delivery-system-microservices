package com.fooddelivery.delivery.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fooddelivery.delivery.agent.model.Agent;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;
    private String authToken;
    private Long orderId;
    // Many Deliveries → One Agent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    @JsonIgnore  // ← prevents infinite loop
    private Agent agent;
    @Column(name = "agent_id",
            insertable = false,
            updatable = false)
    private Long agentId;
    private Long restaurantId;

    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime estimatedDeliveryTime;
}