package org.example.backend.service;

import org.example.backend.model.Activity;
import org.example.backend.model.ActivityCategory;
import org.example.backend.model.Provider;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ProviderRepository providerRepository) {
        this.activityRepository = activityRepository;
        this.providerRepository = providerRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public List<Activity> getActivitiesByProviderId(Long providerId) {
        return activityRepository.findByProviderId(providerId);
    }

    public Activity createActivity(Activity activity, String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with email: " + providerEmail));

        activity.setProvider(provider);
        return activityRepository.save(activity);
    }

    public Optional<Activity> updateActivity(Long id, Activity updatedActivity) {
        return activityRepository.findById(id).map(existingActivity -> {
            existingActivity.setTitle(updatedActivity.getTitle());
            existingActivity.setDescription(updatedActivity.getDescription());
            existingActivity.setDescriptionChild(updatedActivity.getDescriptionChild());
            existingActivity.setCategory(updatedActivity.getCategory());
            existingActivity.setImageUrl(updatedActivity.getImageUrl());
            existingActivity.setLocation(updatedActivity.getLocation());
            existingActivity.setDurationMinutes(updatedActivity.getDurationMinutes());
            existingActivity.setPrice(updatedActivity.getPrice());
            return activityRepository.save(existingActivity);
        });
    }

    public boolean deleteActivity(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Activity> getActivitiesByCategory(String category) {
        ActivityCategory categoryEnum = ActivityCategory.valueOf(category.toUpperCase());
        return activityRepository.findByCategory(categoryEnum);
    }
}