package org.example.backend.service;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.ActivityStatus;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.enums.PriceType;
import org.example.backend.model.Activity;
import org.example.backend.model.Provider;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllActivities() {
        List<Activity> activities = List.of(new Activity(), new Activity());
        when(activityRepository.findAll()).thenReturn(activities);

        List<Activity> result = activityService.getAllActivities();

        assertEquals(2, result.size());
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnApprovedActivities() {
        List<Activity> approved = List.of(new Activity(), new Activity());
        when(activityRepository.findByStatus(ActivityStatus.APPROVED)).thenReturn(approved);

        List<Activity> result = activityService.getApprovedActivities();

        assertEquals(2, result.size());
        verify(activityRepository, times(1)).findByStatus(ActivityStatus.APPROVED);
    }

    @Test
    void shouldReturnActivityById() {
        Activity activity = new Activity();
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        Optional<Activity> result = activityService.getActivityById(1L);

        assertTrue(result.isPresent());
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateActivity() {
        Provider mockProvider = new Provider();
        mockProvider.setId(1L);
        mockProvider.setEmail("provider@example.com");

        Activity mockActivity = new Activity();
        mockActivity.setTitle("Test Activity");

        when(providerRepository.findByEmail("provider@example.com")).thenReturn(Optional.of(mockProvider));
        when(activityRepository.save(any(Activity.class))).thenReturn(mockActivity);

        Activity result = activityService.createActivity(mockActivity, "provider@example.com");

        assertNotNull(result);
        assertEquals("Test Activity", result.getTitle());
        verify(providerRepository, times(1)).findByEmail("provider@example.com");
        verify(activityRepository, times(1)).save(any(Activity.class));
    }


    @Test
    void shouldSearchActivitiesByKeyword() {
        Activity activity1 = new Activity();
        activity1.setTitle("Tapybos dirbtuvės");
        activity1.setDescription("Mokomės tapyti");
        activity1.setPrice(50.0);
        activity1.setCategory(ActivityCategory.ART);
        activity1.setPriceType(PriceType.MONTHLY);
        activity1.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity1.setLocation("Vilnius");

        Activity activity2 = new Activity();
        activity2.setTitle("Muzikos pamokos");
        activity2.setDescription("Mokomės groti");
        activity2.setPrice(40.0);
        activity2.setCategory(ActivityCategory.MUSIC);
        activity2.setPriceType(PriceType.ONE_TIME);
        activity2.setDeliveryMethod(DeliveryMethod.ONLINE);
        activity2.setLocation("Vilnius");

        List<Activity> allActivities = List.of(activity1, activity2);
        when(activityRepository.findAll()).thenReturn(allActivities);
        when(activityRepository.searchByKeyword("tapyba")).thenReturn(List.of(activity1));

        var result = activityService.searchActivities(
                "tapyba", 20.0, 100.0, "Vilnius",
                null, null, "ART", "MONTHLY", "ONSITE"
        );

        assertNotNull(result);
        assertTrue(result.getTopPicks().size() > 0);
        assertTrue(result.getTopPicks().stream().anyMatch(dto -> dto.getTitle().contains("Tapybos")));
    }

    @Test
    void shouldFilterActivitiesCorrectly() {
        Activity activity1 = new Activity();
        activity1.setPrice(50.0);
        activity1.setCategory(ActivityCategory.ART);
        activity1.setPriceType(PriceType.MONTHLY);
        activity1.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity1.setLocation("Vilnius");

        Activity activity2 = new Activity();
        activity2.setPrice(200.0);
        activity2.setCategory(ActivityCategory.SPORTS);
        activity2.setPriceType(PriceType.YEARLY);
        activity2.setDeliveryMethod(DeliveryMethod.ONSITE);
        activity2.setLocation("Kaunas");

        List<Activity> activities = List.of(activity1, activity2);

        when(activityRepository.findByStatus(ActivityStatus.APPROVED)).thenReturn(activities);

        List<ActivityDTO> result = activityService.getFilteredApprovedActivities(
                30.0, 100.0, "Vilnius",
                null, null,
                "ART", "MONTHLY", "ONSITE"
        );

        assertEquals(1, result.size());
        assertEquals("Vilnius", result.get(0).getLocation());
        assertEquals("ART", result.get(0).getCategory().name());
        assertEquals(50.0, result.get(0).getPrice());
    }

    @Test
    void shouldUpdateActivity() {
        Activity existingActivity = new Activity();
        existingActivity.setId(1L);
        existingActivity.setTitle("Old Title");

        ActivityDTO updatedDto = new ActivityDTO();
        updatedDto.setTitle("Updated Title");

        when(activityRepository.findById(1L)).thenReturn(Optional.of(existingActivity));
        when(activityRepository.save(any(Activity.class))).thenReturn(existingActivity);

        Optional<Activity> result = activityService.updateActivity(1L, updatedDto);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        verify(activityRepository, times(1)).findById(1L);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void shouldDeleteActivity() {
        long activityId = 1L;
        when(activityRepository.existsById(activityId)).thenReturn(true);
        boolean result = activityService.deleteActivity(activityId);

        assertTrue(result);
        verify(activityRepository, times(1)).existsById(activityId);
        verify(activityRepository, times(1)).deleteById(activityId);
    }

    @Test
    void shouldUpdateActivityStatus() {
        long activityId = 1L;
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setStatus(ActivityStatus.PENDING);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        boolean result = activityService.updateActivityStatus(activityId, ActivityStatus.APPROVED);

        assertTrue(result);
        assertEquals(ActivityStatus.APPROVED, activity.getStatus());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void shouldGetActivitiesByProviderEmail() {
        String providerEmail = "provider@example.com";
        Provider provider = new Provider();
        provider.setId(1L);
        provider.setEmail(providerEmail);

        Activity activity1 = new Activity();
        activity1.setProvider(provider);
        activity1.setTitle("Activity 1");

        Activity activity2 = new Activity();
        activity2.setProvider(provider);
        activity2.setTitle("Activity 2");

        List<Activity> activities = List.of(activity1, activity2);

        when(providerRepository.findByEmail(providerEmail)).thenReturn(Optional.of(provider));
        when(activityRepository.findByProviderId(provider.getId())).thenReturn(activities);

        List<ActivityDTO> result = activityService.getActivitiesByProviderEmail(providerEmail);

        assertEquals(2, result.size());
        assertEquals("Activity 1", result.get(0).getTitle());
        assertEquals("Activity 2", result.get(1).getTitle());
        verify(providerRepository, times(1)).findByEmail(providerEmail);
        verify(activityRepository, times(1)).findByProviderId(provider.getId());
    }

    @Test
    void shouldGetActivitiesByIds() {
        List<Long> activityIds = List.of(1L, 2L);
        Activity activity1 = new Activity();
        activity1.setId(1L);
        activity1.setTitle("Activity 1");

        Activity activity2 = new Activity();
        activity2.setId(2L);
        activity2.setTitle("Activity 2");

        List<Activity> activities = List.of(activity1, activity2);

        when(activityRepository.findAllById(activityIds)).thenReturn(activities);

        List<ActivityDTO> result = activityService.getActivitiesByIds(activityIds);

        assertEquals(2, result.size());
        assertEquals("Activity 1", result.get(0).getTitle());
        assertEquals("Activity 2", result.get(1).getTitle());
        verify(activityRepository, times(1)).findAllById(activityIds);
    }
}