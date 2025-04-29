package org.example.backend.service;

import org.example.backend.dto.ActivitySimilarityDTO;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.repository.ActivityInteractionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollaborativeFilteringService {

    private final ActivityInteractionRepository interactionRepo;

    public CollaborativeFilteringService(ActivityInteractionRepository interactionRepo) {
        this.interactionRepo = interactionRepo;
    }


    public Map<Long, Map<Long, Double>> buildInteractionMatrix() {
        List<ActivityInteraction> interactions = interactionRepo.findAll();

        Map<Long, Map<Long, Double>> interactionMatrix = new HashMap<>();

        for (ActivityInteraction interaction : interactions) {
            Long activityId = interaction.getActivity().getId();
            Long childId = interaction.getChild().getId();

            double score = 0;
            if (interaction.getViews() > 0) score += 0.3;
            if (interaction.isFavorited()) score += 0.6;
            if (interaction.isRegistered()) score += 1.0;

            if (score == 0) continue;

            interactionMatrix
                    .computeIfAbsent(activityId, k -> new HashMap<>())
                    .put(childId, score);
        }

        return interactionMatrix;
    }

    public Map<Long, List<ActivitySimilarityDTO>> buildSimilarityMatrix(int k) {
        Map<Long, Map<Long, Double>> interactionMatrix = buildInteractionMatrix();
        Map<Long, List<ActivitySimilarityDTO>> similarityMatrix = new HashMap<>();

        for (Long a1 : interactionMatrix.keySet()) {
            Map<Long, Double> vecA = interactionMatrix.get(a1);
            List<ActivitySimilarityDTO> similarities = new ArrayList<>();

            for (Long a2 : interactionMatrix.keySet()) {
                if (a1.equals(a2)) continue;

                Map<Long, Double> vecB = interactionMatrix.get(a2);
                double sim = cosineSimilarity(vecA, vecB);

                similarities.add(new ActivitySimilarityDTO(a2, sim));
            }

            similarities.sort((s1, s2) -> Double.compare(s2.similarity(), s1.similarity()));
            similarityMatrix.put(a1, similarities.subList(0, Math.min(k, similarities.size())));
        }

        return similarityMatrix;
    }

    private double cosineSimilarity(Map<Long, Double> vec1, Map<Long, Double> vec2) {
        Set<Long> commonKeys = new HashSet<>(vec1.keySet());
        commonKeys.retainAll(vec2.keySet());

        double dot = 0;
        for (Long key : commonKeys) {
            dot += vec1.get(key) * vec2.get(key);
        }

        double normA = 0;
        for (double val : vec1.values()) normA += val * val;

        double normB = 0;
        for (double val : vec2.values()) normB += val * val;

        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }

    public List<Long> recommendActivitiesForChild(Long childId, int limit) {
        Map<Long, Map<Long, Double>> interactionMatrix = buildInteractionMatrix();
        Map<Long, List<ActivitySimilarityDTO>> similarityMatrix = buildSimilarityMatrix(5); // top 5 neighbors per activity

        // Step 1: Find all activities the child has interacted with (favorited or registered)
        Set<Long> interactedActivityIds = new HashSet<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : interactionMatrix.entrySet()) {
            Long activityId = entry.getKey();
            Map<Long, Double> childScores = entry.getValue();
            if (childScores.containsKey(childId)) {
                interactedActivityIds.add(activityId);
            }
        }

        System.out.println("Child " + childId + " interacted with activities: " + interactedActivityIds);

        // Step 2: Accumulate scores for similar activities
        Map<Long, Double> recommendationScores = new HashMap<>();

        for (Long interactedActivityId : interactedActivityIds) {
            List<ActivitySimilarityDTO> similarActivities = similarityMatrix.get(interactedActivityId);
            if (similarActivities == null) continue;

            System.out.println("For activity " + interactedActivityId + ", similar activities are:");
            for (ActivitySimilarityDTO similar : similarActivities) {
                Long similarActivityId = similar.activityId();
                if (interactedActivityIds.contains(similarActivityId)) continue; // praleisti jeigu jau buvo matyta

                double existingScore = recommendationScores.getOrDefault(similarActivityId, 0.0);
                recommendationScores.put(similarActivityId, existingScore + similar.similarity());

                System.out.println("  Activity " + similarActivityId + " with similarity score " + similar.similarity());
            }
        }

        // Step 3: Sort by score and return top N
        List<Map.Entry<Long, Double>> sorted = recommendationScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .toList();

        System.out.println("Final recommended activities for child " + childId + ":");
        for (Map.Entry<Long, Double> entry : sorted) {
            System.out.println("  Activity " + entry.getKey() + " with score " + entry.getValue());
        }

        // Step 3: Sort by score and return top N
        return recommendationScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList(); // returns List<Long> of activity IDs

    }


}