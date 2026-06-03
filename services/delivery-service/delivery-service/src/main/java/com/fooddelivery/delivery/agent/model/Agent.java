package com.fooddelivery.delivery.agent.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {
    @Id
    private Long agentId;
    private String name;
    private String phone;
    // ❌ removed profilePhoto
    // ❌ removed currentLatitude
    // ❌ removed currentLongitude
    // ❌ removed locationUpdatedAt
    private boolean isAvailable;
    private Long currentDeliveryId;
    private Integer totalDeliveries;
    private Double totalEarnings;
    private Double rating;
    private Integer totalRatings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ElementCollection
    @CollectionTable(
            name = "agent_reviews",
            joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "review")
    private List<String> recentReviews
            = new ArrayList<>();
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        totalDeliveries = 0;
        totalEarnings = 0.0;
        rating = 0.0;
        totalRatings = 0;
        isAvailable = true;  // ← online by default
    }
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}