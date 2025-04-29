package org.example.backend.service;

import org.example.backend.dto.TimeslotDTO;
import org.example.backend.mapper.TimeslotMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityTimeSlot;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.TimeslotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeslotServiceTest {

    private TimeslotRepository timeslotRepository;
    private ActivityRepository activityRepository;
    private TimeslotService timeslotService;

    @BeforeEach
    void setUp() {
        timeslotRepository = mock(TimeslotRepository.class);
        activityRepository = mock(ActivityRepository.class);
        timeslotService = new TimeslotService(timeslotRepository, activityRepository);
    }

    @Test
    void getTimeslotsByProviderEmail_returnsFilteredList() {
        Activity providerActivity = new Activity();
        providerActivity.setId(1L);
        providerActivity.setProvider(new org.example.backend.model.Provider());
        providerActivity.getProvider().setEmail("provider@example.com");

        ActivityTimeSlot slot1 = new ActivityTimeSlot();
        slot1.setId(1L);
        slot1.setActivity(providerActivity);

        ActivityTimeSlot slot2 = new ActivityTimeSlot();
        slot2.setId(2L);
        slot2.setActivity(null); // Should be filtered out

        ActivityTimeSlot slot3 = new ActivityTimeSlot();
        slot3.setId(3L);
        Activity otherActivity = new Activity();
        otherActivity.setProvider(new org.example.backend.model.Provider());
        otherActivity.getProvider().setEmail("other@example.com");
        slot3.setActivity(otherActivity); // Should be filtered out

        List<ActivityTimeSlot> allSlots = Arrays.asList(slot1, slot2, slot3);
        when(timeslotRepository.findAll()).thenReturn(allSlots);

        List<TimeslotDTO> result = timeslotService.getTimeslotsByProviderEmail("provider@example.com");

        assertEquals(1, result.size());
        assertEquals(slot1.getId(), result.get(0).getId());
    }

    @Test
    void getTimeslotsByActivityId_returnsMappedList() {
        ActivityTimeSlot slot1 = new ActivityTimeSlot();
        slot1.setId(1L);
        ActivityTimeSlot slot2 = new ActivityTimeSlot();
        slot2.setId(2L);

        when(timeslotRepository.findByActivityId(5L)).thenReturn(Arrays.asList(slot1, slot2));

        List<TimeslotDTO> result = timeslotService.getTimeslotsByActivityId(5L);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(1L)));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(2L)));
    }

    @Test
    void createTimeslot_success() {
        TimeslotDTO dto = new TimeslotDTO(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 0, 10);
        Activity activity = new Activity();
        activity.setId(7L);

        when(activityRepository.findById(7L)).thenReturn(Optional.of(activity));
        ActivityTimeSlot slotEntity = TimeslotMapper.toEntity(dto);
        slotEntity.setActivity(activity);
        ActivityTimeSlot savedSlot = new ActivityTimeSlot();
        savedSlot.setId(99L);
        when(timeslotRepository.save(any(ActivityTimeSlot.class))).thenReturn(savedSlot);

        TimeslotDTO result = timeslotService.createTimeslot(7L, dto);

        assertEquals(99L, result.getId());
        verify(timeslotRepository).save(any(ActivityTimeSlot.class));
    }

    @Test
    void createTimeslot_activityNotFound_throws() {
        TimeslotDTO dto = new TimeslotDTO();
        when(activityRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> timeslotService.createTimeslot(123L, dto));
        assertEquals("Activity not found", ex.getMessage());
        verify(timeslotRepository, never()).save(any());
    }

    @Test
    void deleteTimeslot_callsRepository() {
        timeslotService.deleteTimeslot(55L);
        verify(timeslotRepository).deleteById(55L);
    }

    @Test
    void updateTimeslot_success() {
        TimeslotDTO dto = new TimeslotDTO(null, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 5, 20);
        ActivityTimeSlot existing = new ActivityTimeSlot();
        existing.setId(10L);

        when(timeslotRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(timeslotRepository.save(existing)).thenReturn(existing);

        TimeslotDTO result = timeslotService.updateTimeslot(10L, dto);

        assertEquals(existing.getId(), result.getId());
        assertEquals(dto.getStartDateTime(), existing.getStartDateTime());
        assertEquals(dto.getEndDateTime(), existing.getEndDateTime());
        assertEquals(dto.getMaxParticipants(), existing.getMaxParticipants());
        verify(timeslotRepository).save(existing);
    }

    @Test
    void updateTimeslot_notFound_throws() {
        TimeslotDTO dto = new TimeslotDTO();
        when(timeslotRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> timeslotService.updateTimeslot(999L, dto));
        assertEquals("Time slot not found", ex.getMessage());
        verify(timeslotRepository, never()).save(any());
    }
}
