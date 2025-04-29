
package org.example.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.backend.enums.DeliveryMethod;
import org.example.backend.model.Activity;
import org.example.backend.model.ChildProfile;
import org.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

class RecommendationServiceTest {

    @Mock
    private GeoLocationService geoLocationService;

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecommendCloseActivities() throws Exception {
        ChildProfile child = mock(ChildProfile.class);
        User parent = mock(User.class);

        when(child.getParent()).thenReturn(parent);
        when(parent.getAddress()).thenReturn("Vilnius, Lithuania");

        Activity activity1 = mock(Activity.class);
        Activity activity2 = mock(Activity.class);

        when(child.getPreferredDeliveryMethod()).thenReturn(DeliveryMethod.ONSITE);
        when(child.getMaxActivityDuration()).thenReturn(60);

        when(activity1.getLocation()).thenReturn("Vilnius");
        when(activity2.getLocation()).thenReturn("Kaunas");

        when(activity1.getDeliveryMethod()).thenReturn(DeliveryMethod.ONSITE);
        when(activity2.getDeliveryMethod()).thenReturn(DeliveryMethod.ONLINE);

        when(geoLocationService.getCoordinatesFromAddress(eq("Vilnius, Lithuania")))
                .thenReturn(new double[]{54.6892, 25.2798});
        when(geoLocationService.getCoordinatesFromAddress(eq("Vilnius")))
                .thenReturn(new double[]{54.3520, 25.2798});
        when(geoLocationService.getCoordinatesFromAddress(eq("Kaunas")))
                .thenReturn(new double[]{54.8985, 23.9118});

        List<Activity> allActivities = new ArrayList<>(Arrays.asList(activity1, activity2));

        List<Activity> recommendedActivities = recommendationService.recommendCloseActivities(child, allActivities);

        assertEquals(1, recommendedActivities.size());
        assertTrue(recommendedActivities.contains(activity1));
        assertFalse(recommendedActivities.contains(activity2));

        verify(distanceCalculator, times(2)).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(geoLocationService, times(4)).getCoordinatesFromAddress(anyString());
    }

    @Test
    void testGetTravelTimeInMinutes() throws Exception {
        // GIVEN
        when(geoLocationService.getCoordinatesFromAddress("Vilnius, Lithuania")).thenReturn(new double[]{54.6892, 25.2798});
        when(geoLocationService.getCoordinatesFromAddress("Kaunas")).thenReturn(new double[]{54.8985, 23.9118});
        when(distanceCalculator.calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(10.0);

        // WHEN
        double travelTime = recommendationService.getTravelTimeInMinutes("Vilnius, Lithuania", "Kaunas");

        // THEN
        assertEquals(17.142857, travelTime, 0.01); // Expected travel time in minutes
        verify(distanceCalculator, times(1)).calculateDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        verify(geoLocationService, times(2)).getCoordinatesFromAddress(anyString());
    }

    @Test
    void testCalculateDistance_samePoint() {
        DistanceCalculator calculator = new DistanceCalculator();
        double distance = calculator.calculateDistance(54.6892, 25.2798, 54.6892, 25.2798);
        assertEquals(0.0, distance, 0.0001, "Distance between same points should be zero");
    }

    @Test
    void testCalculateDistance_differentPoints() {
        DistanceCalculator calculator = new DistanceCalculator();
        // Vilnius (54.6892, 25.2798) to Kaunas (54.8985, 23.9118)
        double distance = calculator.calculateDistance(54.6892, 25.2798, 54.8985, 23.9118);
        assertTrue(distance > 0, "Distance between Vilnius and Kaunas should be positive");
        // Roughly 92km, allow some margin
        assertEquals(92, distance, 5);
    }

    @Test
    void testCalculateDistance_negativeCoordinates() {
        DistanceCalculator calculator = new DistanceCalculator();
        // Example: Vilnius to a point in southern hemisphere
        double distance = calculator.calculateDistance(54.6892, 25.2798, -33.8688, 151.2093);
        assertTrue(distance > 0, "Distance should be positive for valid coordinates");
    }


}
