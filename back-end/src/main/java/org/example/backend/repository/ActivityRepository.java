package org.example.backend.repository;

import jakarta.validation.constraints.NotNull;
import org.example.backend.model.Activity;
import org.example.backend.model.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProviderId(Long providerId);
    List<Activity> findByCategory(@NotNull(message = "Category cannot be empty") ActivityCategory category);
}