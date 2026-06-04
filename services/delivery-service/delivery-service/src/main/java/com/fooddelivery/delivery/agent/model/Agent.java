package com.fooddelivery.delivery.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fooddelivery.delivery.delivery.model.Delivery;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;        // same as userId from auth-service
    private Long userId;
    private String name;
    private String phone;

    private boolean isAvailable;
    private Long currentDeliveryId;  // null = free


    private Integer totalDeliveries;
    private Double totalEarnings;
    private Double rating;
    private Integer totalRatings;

    @OneToMany(
            mappedBy = "agent",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonIgnore  // ← prevents infinite loop
    @Builder.Default
    private List<Delivery> deliveries
            = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Agent.java — add field
    @ElementCollection
    @CollectionTable(
            name = "agent_reviews",
            joinColumns = @JoinColumn(name = "agent_id"))
    @Column(name = "review")
    @Builder.Default
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
        isAvailable = true;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}