package org.example.backend.controller;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.InteractionDTO;
import org.example.backend.mapper.ActivityMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.repository.ActivityInteractionRepository;
import org.example.backend.service.ActivityInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interactions")
public class ActivityInteractionController {
    private final ActivityInteractionService interactionService;
    private final ActivityInteractionRepository activityInteractionRepository;

    public ActivityInteractionController(ActivityInteractionService interactionService, ActivityInteractionRepository activityInteractionRepository) {
        this.interactionService = interactionService;
        this.activityInteractionRepository = activityInteractionRepository;
    }

    @PostMapping
    public ResponseEntity<Void> recordInteraction(@RequestBody InteractionDTO request) {
        interactionService.recordInteraction(
                request.getChildId(),
                request.getActivityId(),
                request.getInteractionType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/favorite")
    public ResponseEntity<?> toggleFavorite(@RequestBody InteractionDTO request) {
        interactionService.toggleFavorite(request.getChildId(), request.getActivityId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-favorited")
    public ResponseEntity<Boolean> isFavorited(@RequestParam Long childId, @RequestParam Long activityId) {
        boolean favorited = activityInteractionRepository.findByChildIdAndActivityId(childId, activityId)
                .map(ActivityInteraction::isFavorited)
                .orElse(false);
        return ResponseEntity.ok(favorited);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ActivityDTO>> getFavorites(@RequestParam Long childId) {
        List<ActivityInteraction> interactions = activityInteractionRepository.findAllByChildIdAndFavoritedTrue(childId);
        List<Activity> activities = interactions.stream()
                .map(ActivityInteraction::getActivity)
                .toList();
        return ResponseEntity.ok(activities.stream().map(ActivityMapper::toDTO).toList());
    }
}
