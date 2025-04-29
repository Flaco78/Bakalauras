package org.example.backend.repository;

import jakarta.validation.constraints.NotNull;
import org.example.backend.enums.ActivityStatus;
import org.example.backend.model.Activity;
import org.example.backend.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByProviderId(Long providerId);
    List<Activity> findByCategory(@NotNull(message = "Category cannot be empty") ActivityCategory category);
    @Query("SELECT a FROM Activity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Activity> searchByKeyword(@Param("query") String query);
    List<Activity> findByStatus(ActivityStatus status);
}