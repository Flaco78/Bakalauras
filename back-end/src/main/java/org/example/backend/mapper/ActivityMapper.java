package org.example.backend.mapper;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.ReviewDTO;
import org.example.backend.dto.TimeslotDTO;
import org.example.backend.model.Activity;
import org.example.backend.model.Provider;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityMapper {

    private ActivityMapper() {
    }

    public static ActivityDTO toDTO(Activity activity) {
        List<ReviewDTO> reviewDTOs = activity.getReviews().stream()
                .map(review -> new ReviewDTO(review.getComment(), review.getRating()))
                .collect(Collectors.toList());
        List<TimeslotDTO> timeslotDTOs = activity.getTimeSlots().stream()
                .map(timeSlot -> new TimeslotDTO(timeSlot.getId(), timeSlot.getStartDateTime(), timeSlot.getEndDateTime(), timeSlot.getCurrentParticipants(), timeSlot.getMaxParticipants()))
                .collect(Collectors.toList());



        return new ActivityDTO(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getDescriptionChild(),
                activity.getCategory(),
                activity.getImageUrl(),
                activity.getLocation(),
                activity.getDeliveryMethod(),
                activity.getDurationMinutes(),
                activity.getPrice(),
                activity.getPriceType(),
                activity.getProvider() != null ? activity.getProvider().getName() : null,
                activity.getProvider() != null ? activity.getProvider().getEmail() : null,
                activity.getProvider() != null ? activity.getProvider().getCompanyName() : null,
                activity.getProvider() != null ? activity.getProvider().getPhone() : null,
                activity.getProvider() != null ? activity.getProvider().getDescription() : null,
                activity.getProvider() != null ? activity.getProvider().getProviderType() : null,
                activity.getProvider() != null ? activity.getProvider().getId() : null,
                reviewDTOs,
                timeslotDTOs
        );
    }

    public static Activity toEntity(ActivityDTO activityDTO, Provider provider) {
        Activity activity = new Activity();
        activity.setId(activityDTO.getId());
        activity.setTitle(activityDTO.getTitle());
        activity.setDescription(activityDTO.getDescription());
        activity.setDescriptionChild(activityDTO.getDescriptionChild());
        activity.setCategory(activityDTO.getCategory());
        activity.setImageUrl(activityDTO.getImageUrl());
        activity.setLocation(activityDTO.getLocation());
        activity.setDeliveryMethod(activityDTO.getDeliveryMethod());
        activity.setDurationMinutes(activityDTO.getDurationMinutes());
        activity.setPrice(activityDTO.getPrice());
        activity.setPriceType(activityDTO.getPriceType());

        if (provider != null) {
            activity.setProvider(provider);
        }

        return activity;
    }
}