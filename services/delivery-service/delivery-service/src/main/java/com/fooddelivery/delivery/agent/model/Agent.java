package com.fooddelivery.delivery.agent.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    private Long agentId;        // same as userId from auth-service

    private String name;
    private String phone;
    private String profilePhoto;

    private boolean isAvailable;
    private Long currentDeliveryId;  // null = free

    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime locationUpdatedAt;

    private Integer totalDeliveries;
    private Double totalEarnings;
    private Double rating;
    private Integer totalRatings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        totalDeliveries = 0;
        totalEarnings = 0.0;
        rating = 0.0;
        totalRatings = 0;
        isAvailable = false;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}