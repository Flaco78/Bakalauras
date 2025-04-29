package org.example.backend.service;

import org.example.backend.enums.InteractionType;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.ActivityInteractionRepository;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ChildProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityInteractionService {
    private final ChildProfileRepository childProfileRepository;
    private final ActivityRepository activityRepository;
    private final ActivityInteractionRepository activityInteractionRepository;

    public ActivityInteractionService(ChildProfileRepository childProfileRepository, ActivityRepository activityRepository, ActivityInteractionRepository activityInteractionRepository) {
        this.childProfileRepository = childProfileRepository;
        this.activityRepository = activityRepository;
        this.activityInteractionRepository = activityInteractionRepository;
    }

    public void recordInteraction(Long childId, Long activityId, InteractionType type) {
        ChildProfile child = childProfileRepository.findById(childId).orElseThrow();
        Activity activity = activityRepository.findById(activityId).orElseThrow();

        Optional<ActivityInteraction> optional = activityInteractionRepository.findByChildAndActivity(child, activity);
        ActivityInteraction interaction = optional.orElse(new ActivityInteraction());

        interaction.setChild(child);
        interaction.setActivity(activity);

        switch (type) {
            case VIEW -> interaction.setViews(interaction.getViews() + 1);
            case FAVORITE -> interaction.setFavorited(true);
            case REGISTER -> interaction.setRegistered(true);
        }

        activityInteractionRepository.save(interaction);
    }

    public void toggleFavorite(Long childId, Long activityId) {
        ChildProfile child = childProfileRepository.findById(childId).orElseThrow();
        Activity activity = activityRepository.findById(activityId).orElseThrow();

        Optional<ActivityInteraction> existing = activityInteractionRepository.findByChildAndActivity(child, activity);

        ActivityInteraction interaction = existing.orElseGet(() -> {
            ActivityInteraction ai = new ActivityInteraction();
            ai.setChild(child);
            ai.setActivity(activity);
            return ai;
        });

        interaction.setFavorited(!interaction.isFavorited());
        activityInteractionRepository.save(interaction);
    }
}
