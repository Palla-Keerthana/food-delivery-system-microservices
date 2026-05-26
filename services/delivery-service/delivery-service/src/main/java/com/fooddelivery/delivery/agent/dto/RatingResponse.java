package com.fooddelivery.delivery.agent.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponse {

    private Long agentId;
    private Double averageRating;
    private Integer totalRatings;
    private List<String> recentReviews;
}