package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.enums.PriceType;
import org.example.backend.enums.ProviderType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private String descriptionChild;
    private ActivityCategory category;
    private String imageUrl;
    private String location;
    private DeliveryMethod deliveryMethod;
    private int durationMinutes;
    private double price;
    private PriceType priceType;

    private String providerName;
    private String companyName;
    private String providerDescription;
    private ProviderType providerType;

    private Long providerId;

    private List<ReviewDTO> reviews;
    private List<TimeslotDTO> timeslots;

}
