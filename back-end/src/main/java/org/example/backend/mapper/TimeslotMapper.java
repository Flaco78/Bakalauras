package org.example.backend.mapper;

import org.example.backend.dto.TimeslotDTO;
import org.example.backend.model.ActivityTimeSlot;

public class TimeslotMapper {

    public static TimeslotDTO toDTO(ActivityTimeSlot timeslot) {
        if (timeslot == null) return null;
        return new TimeslotDTO(
                timeslot.getId(),
                timeslot.getStartDateTime(),
                timeslot.getEndDateTime(),
                timeslot.getCurrentParticipants(),
                timeslot.getMaxParticipants()
        );
    }

    public static ActivityTimeSlot toEntity(TimeslotDTO dto) {
        ActivityTimeSlot timeslot = new ActivityTimeSlot();
        timeslot.setStartDateTime(dto.getStartDateTime());
        timeslot.setEndDateTime(dto.getEndDateTime());
        timeslot.setMaxParticipants(dto.getMaxParticipants());
        return timeslot;
    }
}