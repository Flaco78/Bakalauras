
package org.example.backend.service;

import org.example.backend.dto.ActivitySimilarityDTO;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.ActivityInteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollaborativeFilteringServiceTest {

    @Mock
    private ActivityInteractionRepository interactionRepo;

    @InjectMocks
    private CollaborativeFilteringService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ActivityInteraction createInteraction(Long activityId, Long childId, int views, boolean favorited, boolean registered) {
        ActivityInteraction interaction = new ActivityInteraction();

        Activity activity = new Activity();
        activity.setId(activityId);

        ChildProfile child = new ChildProfile();
        child.setId(childId);

        interaction.setActivity(activity);
        interaction.setChild(child);
        interaction.setViews(views);
        interaction.setFavorited(favorited);
        interaction.setRegistered(registered);

        return interaction;
    }

    @Test
    void testBuildInteractionMatrix_basic() {
        List<ActivityInteraction> interactions = List.of(
                createInteraction(1L, 101L, 1, true, false), // score: 0.3 + 0.6 = 0.9
                createInteraction(1L, 102L, 0, false, true), // score: 1.0
                createInteraction(2L, 101L, 2, false, false), // score: 0.3
                createInteraction(2L, 103L, 0, true, false) // score: 0.6
        );
        when(interactionRepo.findAll()).thenReturn(interactions);

        Map<Long, Map<Long, Double>> matrix = service.buildInteractionMatrix();

        assertEquals(2, matrix.size());
        assertEquals(2, matrix.get(1L).size());
        assertEquals(2, matrix.get(2L).size());
        assertEquals(0.9, matrix.get(1L).get(101L), 1e-6);
        assertEquals(1.0, matrix.get(1L).get(102L));
        assertEquals(0.3, matrix.get(2L).get(101L));
        assertEquals(0.6, matrix.get(2L).get(103L));
    }

    @Test
    void testBuildSimilarityMatrix_topK() {
        List<ActivityInteraction> interactions = List.of(
                createInteraction(1L, 101L, 1, true, false), // 0.9
                createInteraction(1L, 102L, 0, false, true), // 1.0
                createInteraction(2L, 101L, 2, false, false), // 0.3
                createInteraction(2L, 103L, 0, true, false), // 0.6
                createInteraction(3L, 101L, 1, false, false), // 0.3
                createInteraction(3L, 104L, 0, true, true) // 0.6 + 1.0 = 1.6
        );
        when(interactionRepo.findAll()).thenReturn(interactions);

        Map<Long, List<ActivitySimilarityDTO>> simMatrix = service.buildSimilarityMatrix(2);

        // Each activity should have up to 2 most similar activities
        assertEquals(3, simMatrix.size());
        for (List<ActivitySimilarityDTO> sims : simMatrix.values()) {
            assertTrue(sims.size() <= 2);
        }
        // Similarity scores should be between 0 and 1
        for (List<ActivitySimilarityDTO> sims : simMatrix.values()) {
            for (ActivitySimilarityDTO dto : sims) {
                assertTrue(dto.similarity() >= 0.0 && dto.similarity() <= 1.0);
            }
        }
    }

    @Test
    void testRecommendActivitiesForChild_basic() {
        // Setup: child 101 interacted with activity 1 and 2, child 102 with 1, child 103 with 2, child 104 with 3
        List<ActivityInteraction> interactions = List.of(
                createInteraction(1L, 101L, 1, true, false), // child 101
                createInteraction(1L, 102L, 0, false, true), // child 102
                createInteraction(2L, 101L, 2, false, false), // child 101
                createInteraction(2L, 103L, 0, true, false), // child 103
                createInteraction(3L, 104L, 0, true, true) // child 104
        );
        when(interactionRepo.findAll()).thenReturn(interactions);

        // Recommend for child 101 (has seen 1 and 2, should recommend 3 if similar)
        List<Long> recommendations = service.recommendActivitiesForChild(101L, 2);

        // Should not recommend activities already interacted with
        assertFalse(recommendations.contains(1L));
        assertFalse(recommendations.contains(2L));
        // Should recommend activity 3 if similarity > 0
        assertTrue(recommendations.size() <= 2);
    }

    @Test
    void testRecommendActivitiesForChild_noInteractions() {
        // No interactions at all
        when(interactionRepo.findAll()).thenReturn(Collections.emptyList());

        List<Long> recommendations = service.recommendActivitiesForChild(999L, 3);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testRecommendActivitiesForChild_limit() {
        // Setup: child 101 interacted with activity 1, there are many similar activities
        List<ActivityInteraction> interactions = new ArrayList<>();
        interactions.add(createInteraction(1L, 101L, 1, true, false));
        for (long i = 2; i <= 10; i++) {
            interactions.add(createInteraction(i, 102L, 1, false, false));
        }
        when(interactionRepo.findAll()).thenReturn(interactions);

        List<Long> recommendations = service.recommendActivitiesForChild(101L, 3);
        assertTrue(recommendations.size() <= 3);
    }
}
