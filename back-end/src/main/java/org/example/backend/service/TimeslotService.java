package org.example.backend.service;

import org.example.backend.dto.TimeslotDTO;
import org.example.backend.mapper.TimeslotMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityTimeSlot;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.TimeslotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final ActivityRepository activityRepository;

    public List<TimeslotDTO> getTimeslotsByProviderEmail(String email) {
        List<ActivityTimeSlot> allSlots = timeslotRepository.findAll();

        return allSlots.stream()
                .filter(slot -> {
                    Activity activity = slot.getActivity();
                    return activity != null &&
                            activity.getProvider() != null &&
                            email.equals(activity.getProvider().getEmail());
                })
                .map(TimeslotMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TimeslotService(TimeslotRepository timeslotRepository, ActivityRepository activityRepository) {
        this.timeslotRepository = timeslotRepository;
        this.activityRepository = activityRepository;
    }

    public List<TimeslotDTO> getTimeslotsByActivityId(Long activityId) {
        return timeslotRepository.findByActivityId(activityId).stream()
                .map(TimeslotMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TimeslotDTO createTimeslot(Long activityId, TimeslotDTO dto) {
        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isEmpty()) throw new RuntimeException("Activity not found");

        ActivityTimeSlot slot = TimeslotMapper.toEntity(dto);
        slot.setActivity(activityOpt.get());
        ActivityTimeSlot saved = timeslotRepository.save(slot);
        return TimeslotMapper.toDTO(saved);
    }

    public void deleteTimeslot(Long id) {
        timeslotRepository.deleteById(id);
    }

    public TimeslotDTO updateTimeslot(Long id, TimeslotDTO dto) {
        ActivityTimeSlot existing = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));
        existing.setStartDateTime(dto.getStartDateTime());
        existing.setEndDateTime(dto.getEndDateTime());
        existing.setMaxParticipants(dto.getMaxParticipants());
        ActivityTimeSlot updated = timeslotRepository.save(existing);
        return TimeslotMapper.toDTO(updated);
    }
}