package org.example.backend.service;

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
                    .computeIfAbsent(childId, k -> new HashMap<>())
                    .put(activityId, score);
        }

        return interactionMatrix;
    }

    public Map<Long, List<Long>> buildChildSimilarityMatrix(int k) {
        Map<Long, Map<Long, Double>> childInteractionMatrix = buildInteractionMatrix();
        Map<Long, List<Long>> childSimilarityMatrix = new HashMap<>();

        for (Long childA : childInteractionMatrix.keySet()) {
            Map<Long, Double> vecA = childInteractionMatrix.get(childA);
            List<Map.Entry<Long, Double>> similarities = new ArrayList<>();

            for (Long childB : childInteractionMatrix.keySet()) {
                if (childA.equals(childB)) continue;

                Map<Long, Double> vecB = childInteractionMatrix.get(childB);
                double sim = cosineSimilarity(vecA, vecB);
                System.out.println("Similarity between child " + childA + " and child " + childB + ": " + sim);
                similarities.add(Map.entry(childB, sim));
            }

            similarities.sort((s1, s2) -> Double.compare(s2.getValue(), s1.getValue()));
            List<Long> topSimilarChildren = similarities.stream()
                    .limit(k)
                    .map(Map.Entry::getKey)
                    .toList();
            System.out.println("Top similar children for child " + childA + ": " + topSimilarChildren);
            childSimilarityMatrix.put(childA, topSimilarChildren);
        }

        return childSimilarityMatrix;
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
        Map<Long, Map<Long, Double>> childInteractionMatrix = buildInteractionMatrix();
        Map<Long, List<Long>> childSimilarityMatrix = buildChildSimilarityMatrix(5); // top 5 similar children

        // Veiklos, kurias vaikas jau matė
        Set<Long> viewedActivities = childInteractionMatrix.getOrDefault(childId, new HashMap<>()).keySet();
        Map<Long, Double> recommendationScores = new HashMap<>();

        // Panašūs vaikai
        List<Long> similarChildren = childSimilarityMatrix.getOrDefault(childId, new ArrayList<>());
        System.out.println("Similar children for child " + childId + ": " + similarChildren);
        System.out.println("Viewed activities for child " + childId + ": " + viewedActivities);
        for (Long similarChild : similarChildren) {
            Map<Long, Double> similarChildActivities = childInteractionMatrix.get(similarChild);
            for (Map.Entry<Long, Double> entry : similarChildActivities.entrySet()) {
                Long activityId = entry.getKey();
                double score = entry.getValue();

                // Praleisti, jei vaikas jau matė veiklą
                if (viewedActivities.contains(activityId)) continue;

                recommendationScores.put(activityId,
                        recommendationScores.getOrDefault(activityId, 0.0) + score);
            }
        }

        System.out.println("Recommendation scores for child " + childId + ": " + recommendationScores);
        // Surūšiuoti pagal aukščiausią bendrą balą
        return recommendationScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

}