package org.example.backend.repository;

import org.example.backend.model.Activity;
import org.example.backend.model.ActivityInteraction;
import org.example.backend.model.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ActivityInteractionRepository extends JpaRepository<ActivityInteraction, Long> {
    Optional<ActivityInteraction> findByChildAndActivity(ChildProfile child, Activity activity);
    Optional<ActivityInteraction> findByChildIdAndActivityId(Long childId, Long activityId);
    List<ActivityInteraction> findAllByChildIdAndFavoritedTrue(Long childId);
    List<ActivityInteraction> findAllByActivityId(Long activityId);
    @Query("""
    SELECT ai.activity FROM ActivityInteraction ai
    GROUP BY ai.activity
    ORDER BY SUM(ai.views) DESC
    """)
    List<Activity> findMostViewed(Pageable pageable);

    @Query("""
    SELECT ai.activity FROM ActivityInteraction ai
    WHERE ai.favorited = true
    GROUP BY ai.activity
    ORDER BY COUNT(ai.id) DESC
    """)
    List<Activity> findMostFavorited(Pageable pageable);

    @Query("""
    SELECT ai.activity FROM ActivityInteraction ai
    WHERE ai.registered = true
    GROUP BY ai.activity
    ORDER BY COUNT(ai.id) DESC
    """)
    List<Activity> findMostRegistered(Pageable pageable);

}
