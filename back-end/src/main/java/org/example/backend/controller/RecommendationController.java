package org.example.backend.controller;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.mapper.ActivityMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.ActivityInteractionRepository;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ChildProfileRepository;
import org.example.backend.service.CollaborativeFilteringService;
import org.example.backend.service.RecommendationService;
import org.example.backend.service.VectorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final ChildProfileRepository childRepo;
    private final ActivityRepository activityRepository;
    private final CollaborativeFilteringService filteringService;
    private final ActivityRepository activityRepo;
    private final ActivityInteractionRepository activityInteractionRepository;
    private final RecommendationService recommendationService;


    @Autowired
    public RecommendationController(ChildProfileRepository childRepo, ActivityRepository activityRepo, CollaborativeFilteringService filteringService, ActivityRepository activityRepo1, ActivityInteractionRepository activityInteractionRepository, RecommendationService recommendationService) {
        this.childRepo = childRepo;
        this.activityRepository = activityRepo;
        this.filteringService = filteringService;
        this.activityRepo = activityRepo1;
        this.activityInteractionRepository = activityInteractionRepository;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/content-based/{childId}")
    public ResponseEntity<List<ActivityDTO>> recommendContentBased(@PathVariable Long childId) {
        ChildProfile child = childRepo.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        List<Integer> childVector = VectorBuilder.buildChildVector(child);

        List<Activity> allActivities = activityRepository.findAll();
        List<ActivityScore> scored = new ArrayList<>();

        for (Activity activity : allActivities) {
            List<Integer> activityVector = VectorBuilder.buildActivityVector(activity);
            double similarity = VectorBuilder.cosineSimilarity(childVector, activityVector);
            scored.add(new ActivityScore(activity, similarity));
        }

        scored.sort((a, b) -> Double.compare(b.similarity, a.similarity));

        List<ActivityDTO> recommendations = scored.stream()
                .map(a -> ActivityMapper.toDTO(a.activity))
                .limit(8)
                .collect(Collectors.toList());

        return ResponseEntity.ok(recommendations);
    }

    private static class ActivityScore {
        Activity activity;
        double similarity;
        ActivityScore(Activity activity, double similarity) {
            this.activity = activity;
            this.similarity = similarity;
        }
    }

    @GetMapping("/collaboration-filtering/{childId}")
    public ResponseEntity<List<ActivityDTO>> getRecommendations(@PathVariable Long childId,
                                                                @RequestParam(defaultValue = "5") int limit) {
        List<Long> activityIds = filteringService.recommendActivitiesForChild(childId, limit);
        List<Activity> activities = activityRepo.findAllById(activityIds);

        List<ActivityDTO> activityDTOs = activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activityDTOs);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ActivityDTO>> getPopularActivities(
            @RequestParam(defaultValue = "view") String type,
            @RequestParam(defaultValue = "8") int limit
    ) {
        List<Activity> popular;
        Pageable pageable = PageRequest.of(0, limit);

        switch (type.toLowerCase()) {
            case "favorite" -> popular = activityInteractionRepository.findMostFavorited(pageable);
            case "registered" -> popular = activityInteractionRepository.findMostRegistered(pageable);
            default -> popular = activityInteractionRepository.findMostViewed(pageable);
        }

        List<ActivityDTO> dtoList = popular.stream()
                .map(ActivityMapper::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/nearby/{childId}")
    public ResponseEntity<List<ActivityDTO>> getNearbyActivities(@PathVariable Long childId) {
        ChildProfile child = childRepo.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        List<Activity> allActivities = activityRepo.findAll();
        List<Activity> nearby = recommendationService.recommendCloseActivities(child, allActivities);

        List<ActivityDTO> result = nearby.stream()
                .map(ActivityMapper::toDTO)
                .limit(8)
                .toList();

        return ResponseEntity.ok(result);
    }

}