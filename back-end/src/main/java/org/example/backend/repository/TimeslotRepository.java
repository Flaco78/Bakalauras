package org.example.backend.repository;

import org.example.backend.model.ActivityTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeslotRepository extends JpaRepository<ActivityTimeSlot, Long> {
    List<ActivityTimeSlot> findByActivityId(Long activityId);
}