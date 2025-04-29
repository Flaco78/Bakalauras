package org.example.backend.controller;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.SearchResultDTO;
import org.example.backend.enums.ActivityStatus;
import org.example.backend.mapper.ActivityMapper;
import org.example.backend.model.Activity;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.model.Provider;
import org.example.backend.service.ActivityService;
import org.example.backend.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;
    private final ProviderService providerService;

    @Autowired
    public ActivityController(ActivityService activityService, ProviderService providerService) {
        this.activityService = activityService;
        this.providerService = providerService;
    }

    // Gauti visas veiklas
    @GetMapping("/all")
    public ResponseEntity<List<ActivityDTO>> getApprovedActivities() {
        List<Activity> activities = activityService.getApprovedActivities();
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activityDTOs);
    }

    @GetMapping("/all-filtered")
    public ResponseEntity<List<ActivityDTO>> getAllFilteredActivities(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priceType,
            @RequestParam(required = false) String deliveryMethod
    ) {
        List<ActivityDTO> activities = activityService.getFilteredApprovedActivities(
                minPrice, maxPrice, location,
                minDuration, maxDuration, category, priceType, deliveryMethod
        );
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/every")
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activityDTOs);
    }


    // Gauti veiklas pagal tiekėjo ID
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByProviderId(@PathVariable Long providerId) {
        List<Activity> activities = activityService.getActivitiesByProviderId(providerId);
        List<ActivityDTO> dtos = activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ActivityDTO>> getMyActivities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName(); // works for both PROVIDER and USER
        List<ActivityDTO> activities = activityService.getActivitiesByProviderEmail(email);
        return ResponseEntity.ok(activities);
    }

    // Sukurti veiklą (prisijungusiam tiekėjui)
    @PostMapping("/create")
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody Activity activity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String providerEmail = authentication.getName();
        Activity createdActivity = activityService.createActivity(activity, providerEmail);
        ActivityDTO dto = ActivityMapper.toDTO(createdActivity);
        return ResponseEntity.ok(dto);
    }

    // Atnaujinti veiklą
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO dto) {
        return activityService.updateActivity(id, dto)
                .map(ActivityMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ActivityDTO> updateActivityByAdmin(@PathVariable Long id, @RequestBody ActivityDTO activityDto) {
        Provider provider = providerService.getProviderById(activityDto.getProviderId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with id: " + activityDto.getProviderId()));

        Optional<Activity> updated = activityService.updateActivityAsAdmin(id, activityDto, provider);

        return updated.map(ActivityMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ištrinti veiklą
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        if (activityService.deleteActivity(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Gauti visas veiklų kategorijas
    @GetMapping("/categories")
    public ResponseEntity<ActivityCategory[]> getAllCategories() {
        return ResponseEntity.ok(ActivityCategory.values());
    }

    // Gauti veiklas pagal kategoriją
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCategory(@PathVariable String category) {
        List<Activity> activities = activityService.getActivitiesByCategory(category.toUpperCase());

        // Konvertuojame Activity į ActivityDTO
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(ActivityMapper::toDTO)  // Naudojame mapper'į
                .collect(Collectors.toList());

        return ResponseEntity.ok(activityDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        Optional<Activity> optionalActivity = activityService.getActivityById(id);
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            ActivityDTO dto = ActivityMapper.toDTO(activity);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build(); // Jei veikla nerasta
        }
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ActivityDTO> createActivityByAdmin(@RequestBody ActivityDTO activityDto) {

        Provider provider = providerService.getProviderById(activityDto.getProviderId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with id: " + activityDto.getProviderId()));
        Activity activity = activityService.createActivityForProvider(activityDto, provider);
        System.out.println("Received providerId: " + activityDto.getProviderId());
        return ResponseEntity.ok(ActivityMapper.toDTO(activity));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> searchActivities(
            @RequestParam String query,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priceType,
            @RequestParam(required = false) String deliveryMethod
    ) {
        SearchResultDTO result = activityService.searchActivities(
                query, minPrice, maxPrice, location,
                minDuration, maxDuration, category, priceType, deliveryMethod
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ActivityDTO>> getPendingActivities() {
        List<Activity> pendingActivities = activityService.getActivitiesByStatus(ActivityStatus.PENDING);
        return ResponseEntity.ok(pendingActivities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Void> approveActivity(@PathVariable Long id) {
        boolean success = activityService.updateActivityStatus(id, ActivityStatus.APPROVED);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Void> rejectActivity(@PathVariable Long id) {
        boolean success = activityService.updateActivityStatus(id, ActivityStatus.REJECTED);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-ids")
    public List<ActivityDTO> getActivitiesByIds(@RequestParam List<Long> ids) {
        return activityService.getActivitiesByIds(ids);
    }
}