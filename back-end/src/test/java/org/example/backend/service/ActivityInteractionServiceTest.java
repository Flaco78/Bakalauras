package org.example.backend.service;

import org.example.backend.enums.InteractionType;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.ActivityInteractionRepository;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.ChildProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityInteractionServiceTest {

    private ActivityInteractionRepository activityInteractionRepository;
    private ActivityInteractionService service;

    private ChildProfile child;
    private Activity activity;

    @BeforeEach
    void setUp() {
        ChildProfileRepository childProfileRepository = mock(ChildProfileRepository.class);
        ActivityRepository activityRepository = mock(ActivityRepository.class);
        activityInteractionRepository = mock(ActivityInteractionRepository.class);
        service = new ActivityInteractionService(childProfileRepository, activityRepository, activityInteractionRepository);

        child = new ChildProfile();
        child.setId(1L);
        activity = new Activity();
        activity.setId(2L);

        when(childProfileRepository.findById(1L)).thenReturn(Optional.of(child));
        when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
    }

    @Test
    void recordInteraction_shouldCreateNewInteraction_whenNoneExists() {
        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.empty());

        service.recordInteraction(1L, 2L, InteractionType.VIEW);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(activityInteractionRepository).save(captor.capture());
        ActivityInteraction saved = captor.getValue();

        assertEquals(child, saved.getChild());
        assertEquals(activity, saved.getActivity());
        assertEquals(1, saved.getViews());
        assertFalse(saved.isFavorited());
        assertFalse(saved.isRegistered());
    }

    @Test
    void recordInteraction_shouldUpdateExistingInteraction() {
        ActivityInteraction existing = new ActivityInteraction();
        existing.setChild(child);
        existing.setActivity(activity);
        existing.setViews(3);
        existing.setFavorited(false);
        existing.setRegistered(false);

        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.of(existing));

        service.recordInteraction(1L, 2L, InteractionType.VIEW);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(activityInteractionRepository).save(captor.capture());
        ActivityInteraction saved = captor.getValue();

        assertEquals(4, saved.getViews());
    }

    @Test
    void recordInteraction_shouldSetFavoritedAndRegistered() {
        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.empty());

        // FAVORITE
        service.recordInteraction(1L, 2L, InteractionType.FAVORITE);
        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(activityInteractionRepository, times(1)).save(captor.capture());
        ActivityInteraction saved = captor.getValue();
        assertTrue(saved.isFavorited());

        // REGISTER
        reset(activityInteractionRepository);
        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.empty());
        service.recordInteraction(1L, 2L, InteractionType.REGISTER);
        verify(activityInteractionRepository, times(1)).save(captor.capture());
        ActivityInteraction saved2 = captor.getValue();
        assertTrue(saved2.isRegistered());
    }

    @Test
    void toggleFavorite_shouldToggleFavorited_whenInteractionExists() {
        ActivityInteraction existing = new ActivityInteraction();
        existing.setChild(child);
        existing.setActivity(activity);
        existing.setFavorited(false);

        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.of(existing));

        service.toggleFavorite(1L, 2L);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(activityInteractionRepository).save(captor.capture());
        ActivityInteraction saved = captor.getValue();

        assertTrue(saved.isFavorited());

        // Toggle again
        existing.setFavorited(true);
        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.of(existing));
        service.toggleFavorite(1L, 2L);
        verify(activityInteractionRepository, times(2)).save(captor.capture());
        ActivityInteraction saved2 = captor.getValue();
        assertFalse(saved2.isFavorited());
    }

    @Test
    void toggleFavorite_shouldCreateNewInteraction_whenNoneExists() {
        when(activityInteractionRepository.findByChildAndActivity(child, activity)).thenReturn(Optional.empty());

        service.toggleFavorite(1L, 2L);

        ArgumentCaptor<ActivityInteraction> captor = ArgumentCaptor.forClass(ActivityInteraction.class);
        verify(activityInteractionRepository).save(captor.capture());
        ActivityInteraction saved = captor.getValue();

        assertTrue(saved.isFavorited());
        assertEquals(child, saved.getChild());
        assertEquals(activity, saved.getActivity());
    }
}
