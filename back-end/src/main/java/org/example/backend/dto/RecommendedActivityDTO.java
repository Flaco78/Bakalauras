package org.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RecommendedActivityDTO {
    private ActivityDTO activity;
    private double travelTimeMinutes;
    private double distanceKm;

    public RecommendedActivityDTO(ActivityDTO activity, double travelTime, double distance) {
        this.activity = activity;
        this.travelTimeMinutes = travelTime;
        this.distanceKm = distance;
    }
}
