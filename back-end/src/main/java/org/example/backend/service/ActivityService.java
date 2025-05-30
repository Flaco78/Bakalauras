package org.example.backend.service;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.SearchResultDTO;
import org.example.backend.enums.ActivityStatus;
import org.example.backend.mapper.ActivityMapper;
import org.example.backend.model.Activity;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.model.Provider;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ProviderRepository providerRepository;
    private static final Map<String, ActivityCategory> keywordToCategory = Map.ofEntries(
            Map.entry("robotika", ActivityCategory.CODING),
            Map.entry("programavimas", ActivityCategory.CODING),
            Map.entry("technologijos", ActivityCategory.CODING),
            Map.entry("kodas", ActivityCategory.CODING),
            Map.entry("piešimas", ActivityCategory.ART),
            Map.entry("tapymas", ActivityCategory.ART),
            Map.entry("sportas", ActivityCategory.SPORTS),
            Map.entry("futbolas", ActivityCategory.SPORTS),
            Map.entry("mokslas", ActivityCategory.SCIENCE),
            Map.entry("eksperimentas", ActivityCategory.SCIENCE),
            Map.entry("muzika", ActivityCategory.MUSIC),
            Map.entry("gitara", ActivityCategory.MUSIC),
            Map.entry("pianinas", ActivityCategory.MUSIC)
    );

    @Autowired
    public ActivityService(ActivityRepository activityRepository, ProviderRepository providerRepository) {
        this.activityRepository = activityRepository;
        this.providerRepository = providerRepository;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getApprovedActivities() {
        return activityRepository.findByStatus(ActivityStatus.APPROVED);
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

    public Optional<Activity> updateActivity(Long id, ActivityDTO dto) {
        return activityRepository.findById(id).map(existingActivity -> {
            existingActivity.setTitle(dto.getTitle());
            existingActivity.setDescription(dto.getDescription());
            existingActivity.setDescriptionChild(dto.getDescriptionChild());
            existingActivity.setCategory(dto.getCategory());
            existingActivity.setImageUrl(dto.getImageUrl());
            existingActivity.setLocation(dto.getLocation());
            existingActivity.setDeliveryMethod(dto.getDeliveryMethod());
            existingActivity.setDurationMinutes(dto.getDurationMinutes());
            existingActivity.setPrice(dto.getPrice());
            existingActivity.setPriceType(dto.getPriceType());
            return activityRepository.save(existingActivity);
        });
    }

    public Optional<Activity> updateActivityAsAdmin(Long id, ActivityDTO dto, Provider provider) {
        return activityRepository.findById(id).map(activity -> {
            activity.setTitle(dto.getTitle());
            activity.setDescription(dto.getDescription());
            activity.setDescriptionChild(dto.getDescriptionChild());
            activity.setImageUrl(dto.getImageUrl());
            activity.setLocation(dto.getLocation());
            activity.setCategory(dto.getCategory());
            activity.setPrice(dto.getPrice());
            activity.setPriceType(dto.getPriceType());
            activity.setDeliveryMethod(dto.getDeliveryMethod());
            activity.setDurationMinutes(dto.getDurationMinutes());
            activity.setProvider(provider); // svarbiausia dalis
            return activityRepository.save(activity);
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


    public SearchResultDTO searchActivities(String query, Double minPrice, Double maxPrice, String location,
                                            Integer minDuration, Integer maxDuration,
                                            String category, String priceType, String deliveryMethod) {
        List<Activity> allActivities = activityRepository.findAll();
        Map<Long, ScoredActivity> scoredMap = new java.util.HashMap<>();
        LevenshteinDistance levenshtein = new LevenshteinDistance();



        // 1. Tiksli kategorija
        if (keywordToCategory.containsKey(query.toLowerCase())) {
            ActivityCategory keywordCategory = keywordToCategory.get(query.toLowerCase());
            List<Activity> byKeywordCategory = activityRepository.findByCategory(keywordCategory);
            for (Activity activity : byKeywordCategory) {
                scoredMap.merge(
                        activity.getId(),
                        new ScoredActivity(activity, 100),
                        (oldVal, newVal) -> oldVal.score < newVal.score ? newVal : oldVal
                );
            }
        }

        // 2.5 Papildoma - repo paieška pagal title, description, location ir kt.
        List<Activity> repoMatches = activityRepository.searchByKeyword(query);
        for (Activity activity : repoMatches) {
            scoredMap.merge(
                    activity.getId(),
                    new ScoredActivity(activity, 70),
                    (oldVal, newVal) -> oldVal.score < newVal.score ? newVal : oldVal
            );
        }

        // 2.7 Jeigu pavadinimą query atitinka beveik pilnai
        for (Activity activity : allActivities) {
            if (activity.getTitle().equalsIgnoreCase(query)) {
                scoredMap.merge(
                        activity.getId(),
                        new ScoredActivity(activity, 95),
                        (oldVal, newVal) -> oldVal.score < newVal.score ? newVal : oldVal
                );
            } else if (activity.getTitle().toLowerCase().startsWith(query.toLowerCase())) {
                scoredMap.merge(
                        activity.getId(),
                        new ScoredActivity(activity, 90),
                        (oldVal, newVal) -> oldVal.score < newVal.score ? newVal : oldVal
                );
            }
        }

        // 3. Levenshtein panašumo paieška – per žodžius
        for (Activity activity : allActivities) {
            String combinedText = (activity.getTitle() + " " +
                    activity.getDescription() + " " +
                    activity.getDescriptionChild()).toLowerCase();

            String[] words = combinedText.split("\\s+"); // suskaldom į žodžius
            int bestScore = 0;

            for (String word : words) {
                int distance = levenshtein.apply(query.toLowerCase(), word);
                int maxLen = Math.max(query.length(), word.length());
                if (maxLen == 0) continue; // apsauga nuo 0
                int localScore = (int) ((1.0 - ((double) distance / maxLen)) * 80);

                if (localScore > bestScore) {
                    bestScore = localScore;
                }
            }

            if (bestScore > 30) {
                scoredMap.merge(
                        activity.getId(),
                        new ScoredActivity(activity, bestScore),
                        (oldVal, newVal) -> oldVal.score < newVal.score ? newVal : oldVal
                );
            }
        }

        List<ScoredActivity> filteredResults = filterActivities(
                new ArrayList<>(scoredMap.values()), minPrice, maxPrice, location,
                minDuration, maxDuration, category, priceType, deliveryMethod
        );
        filteredResults.sort((a, b) -> Integer.compare(b.score, a.score));
        System.out.println("Filtruoti rezultatai: " + filteredResults.size());


        List<ActivityDTO> topPicks = new ArrayList<>();
        List<ActivityDTO> otherResults = new ArrayList<>();



        for (ScoredActivity sa : filteredResults) {
            ActivityDTO dto = ActivityMapper.toDTO(sa.activity);
            if (sa.score >= 70) {
                topPicks.add(dto);
            } else {
                otherResults.add(dto);
            }
        }

        topPicks.sort((a, b) -> b.getTitle().compareTo(a.getTitle()));
        otherResults.sort((a, b) -> b.getTitle().compareTo(a.getTitle()));

        return new SearchResultDTO(topPicks, otherResults);
    }
    private static class ScoredActivity {

        Activity activity;
        int score;

        ScoredActivity(Activity activity, int score) {
            this.activity = activity;
            this.score = score;
        }
    }

    private List<ScoredActivity> filterActivities(List<ScoredActivity> scoredResults,
                                                  Double minPrice, Double maxPrice, String location,
                                                  Integer minDuration, Integer maxDuration,
                                                  String category, String priceType, String deliveryMethod) {
        return scoredResults.stream()
                .filter(sa -> minPrice == null || sa.activity.getPrice() >= minPrice)
                .filter(sa -> maxPrice == null || sa.activity.getPrice() <= maxPrice)
                .filter(sa -> location == null || location.isEmpty() || (
                        sa.activity.getLocation() != null &&
                                sa.activity.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(sa -> minDuration == null || sa.activity.getDurationMinutes() >= minDuration)
                .filter(sa -> maxDuration == null || sa.activity.getDurationMinutes() <= maxDuration)
                .filter(sa -> category == null || category.isEmpty() ||
                        sa.activity.getCategory().name().equalsIgnoreCase(category))
                .filter(sa -> priceType == null || priceType.isEmpty() ||
                        sa.activity.getPriceType().name().equalsIgnoreCase(priceType))
                .filter(sa -> deliveryMethod == null || deliveryMethod.isEmpty() ||
                        sa.activity.getDeliveryMethod().name().equalsIgnoreCase(deliveryMethod))
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> getFilteredApprovedActivities(
            Double minPrice, Double maxPrice, String location,
            Integer minDuration, Integer maxDuration,
            String category, String priceType, String deliveryMethod) {

        List<Activity> approvedActivities = activityRepository.findByStatus(ActivityStatus.APPROVED);

        List<Activity> filtered = approvedActivities.stream()
                .filter(a -> minPrice == null || a.getPrice() >= minPrice)
                .filter(a -> maxPrice == null || a.getPrice() <= maxPrice)
                .filter(a -> location == null || location.isEmpty() || (
                        a.getLocation() != null &&
                                a.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(a -> minDuration == null || a.getDurationMinutes() >= minDuration)
                .filter(a -> maxDuration == null || a.getDurationMinutes() <= maxDuration)
                .filter(a -> category == null || category.isEmpty() || (
                        a.getCategory() != null &&
                                a.getCategory().name().equalsIgnoreCase(category)))
                .filter(a -> priceType == null || priceType.isEmpty() || (
                        a.getPriceType() != null &&
                                a.getPriceType().name().equalsIgnoreCase(priceType)))
                .filter(a -> deliveryMethod == null || deliveryMethod.isEmpty() || (
                        a.getDeliveryMethod() != null &&
                                a.getDeliveryMethod().name().equalsIgnoreCase(deliveryMethod)))
                .collect(Collectors.toList());

        return filtered.stream().map(ActivityMapper::toDTO).collect(Collectors.toList());
    }

    public Activity createActivityForProvider(ActivityDTO dto, Provider provider) {
        Activity activity = ActivityMapper.toEntity(dto, provider);
        activity.setProvider(provider);
        return activityRepository.save(activity);
    }

    public List<Activity> getActivitiesByStatus(ActivityStatus status) {
        return activityRepository.findByStatus(status);
    }

    public boolean updateActivityStatus(Long id, ActivityStatus status) {
        Optional<Activity> activityOpt = activityRepository.findById(id);
        if (activityOpt.isPresent()) {
            Activity activity = activityOpt.get();
            activity.setStatus(status);
            activityRepository.save(activity);
            return true;
        }
        return false;
    }

    public List<ActivityDTO> getActivitiesByProviderEmail(String email) {
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        List<Activity> activities = activityRepository.findByProviderId(provider.getId());
        return activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> getActivitiesByIds(List<Long> ids) {
        return activityRepository.findAllById(ids).stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
    }
}