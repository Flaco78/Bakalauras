
package org.example.backend.service;

import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.model.Activity;
import org.example.backend.model.ChildProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VectorBuilderTest {

    @Test
    void testBuildActivityVector_onlineShort() {
        Activity activity = new Activity();
        activity.setCategory(ActivityCategory.SPORTS);
        activity.setDeliveryMethod(DeliveryMethod.ONLINE);
        activity.setDurationMinutes(20);

        List<Integer> vector = VectorBuilder.buildActivityVector(activity);

        // 6 categories, only SPORTS is 1
        assertEquals(6, ActivityCategory.values().length);
        assertEquals(1, vector.get(0)); // SPORTS
        assertEquals(0, vector.get(1)); // MUSIC
        // ... (rest are 0)
        // Delivery method: ONLINE=1, ONSITE=0
        assertEquals(1, vector.get(6));
        assertEquals(0, vector.get(7));
        // Duration: short (<30)
        assertEquals(List.of(1, 0, 0), vector.subList(8, 11));
    }

    @Test
    void testBuildActivityVector_onsiteMedium() {
        Activity activity = new Activity();
        activity.setCategory(ActivityCategory.ART);
        activity.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity.setDurationMinutes(45);

        List<Integer> vector = VectorBuilder.buildActivityVector(activity);

        // ART is 1
        assertEquals(0, vector.get(0)); // SPORTS
        assertEquals(0, vector.get(1)); // MUSIC
        assertEquals(1, vector.get(2)); // ART
        // Delivery method: ONLINE=0, ONSITE=1
        assertEquals(0, vector.get(6));
        assertEquals(1, vector.get(7));
        // Duration: medium (<=60)
        assertEquals(List.of(0, 1, 0), vector.subList(8, 11));
    }

    @Test
    void testBuildActivityVector_long() {
        Activity activity = new Activity();
        activity.setCategory(ActivityCategory.CODING);
        activity.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity.setDurationMinutes(120);

        List<Integer> vector = VectorBuilder.buildActivityVector(activity);

        // CODING is 1
        assertEquals(0, vector.get(0)); // SPORTS
        assertEquals(0, vector.get(1)); // MUSIC
        assertEquals(0, vector.get(2)); // ART
        assertEquals(0, vector.get(3)); // SCIENCE
        assertEquals(1, vector.get(4)); // CODING
        // Delivery method: ONLINE=0, ONSITE=1
        assertEquals(0, vector.get(6));
        assertEquals(1, vector.get(7));
        // Duration: long (>60)
        assertEquals(List.of(0, 0, 1), vector.subList(8, 11));
    }

    @Test
    void testBuildChildVector_preferences() {
        ChildProfile child = new ChildProfile();
        child.setInterests(new HashSet<>(Arrays.asList(ActivityCategory.SPORTS, ActivityCategory.ART)));
        child.setPreferredDeliveryMethod(DeliveryMethod.ONLINE);
        child.setMaxActivityDuration(30);
        child.setName("Test");
        child.setBirthDate(LocalDate.of(2015, 1, 1));

        List<Integer> vector = VectorBuilder.buildChildVector(child);

        // Interests: SPORTS and ART are 1
        assertEquals(1, vector.get(0)); // SPORTS
        assertEquals(0, vector.get(1)); // MUSIC
        assertEquals(1, vector.get(2)); // ART
        // Delivery method: ONLINE=1, ONSITE=0
        assertEquals(1, vector.get(6));
        assertEquals(0, vector.get(7));
        // Max duration: 30 (short and medium)
        assertEquals(1, vector.get(8)); // <=30
        assertEquals(1, vector.get(9)); // <=60
        assertEquals(0, vector.get(10)); // >60
    }

    @Test
    void testBuildChildVector_longPreference() {
        ChildProfile child = new ChildProfile();
        child.setInterests(new HashSet<>(Collections.singletonList(ActivityCategory.SCIENCE)));
        child.setPreferredDeliveryMethod(DeliveryMethod.ONSITE);
        child.setMaxActivityDuration(90);
        child.setName("Test");
        child.setBirthDate(LocalDate.of(2015, 1, 1));

        List<Integer> vector = VectorBuilder.buildChildVector(child);

        // Interests: only SCIENCE is 1
        assertEquals(0, vector.get(0)); // SPORTS
        assertEquals(0, vector.get(1)); // MUSIC
        assertEquals(0, vector.get(2)); // ART
        assertEquals(1, vector.get(3)); // SCIENCE
        // Delivery method: ONLINE=0, ONSITE=1
        assertEquals(0, vector.get(6));
        assertEquals(1, vector.get(7));
        // Max duration: >60
        assertEquals(0, vector.get(8)); // <=30
        assertEquals(0, vector.get(9)); // <=60
        assertEquals(1, vector.get(10)); // >60
    }

    @Test
    void testCosineSimilarity_identicalVectors() {
        List<Integer> v1 = Arrays.asList(1, 0, 1, 0, 1);
        List<Integer> v2 = Arrays.asList(1, 0, 1, 0, 1);

        double sim = VectorBuilder.cosineSimilarity(v1, v2);
        assertEquals(1.0, sim, 1e-6);
    }

    @Test
    void testCosineSimilarity_orthogonalVectors() {
        List<Integer> v1 = Arrays.asList(1, 0, 0, 0, 0);
        List<Integer> v2 = Arrays.asList(0, 1, 0, 0, 0);

        double sim = VectorBuilder.cosineSimilarity(v1, v2);
        assertEquals(0.0, sim, 1e-6);
    }

    @Test
    void testCosineSimilarity_partialOverlap() {
        List<Integer> v1 = Arrays.asList(1, 1, 0, 0, 0);
        List<Integer> v2 = Arrays.asList(1, 0, 1, 0, 0);

        double sim = VectorBuilder.cosineSimilarity(v1, v2);
        // dot = 1, normA = sqrt(2), normB = sqrt(2), sim = 1/2 = 0.5
        assertEquals(0.5, sim, 1e-6);
    }
}
