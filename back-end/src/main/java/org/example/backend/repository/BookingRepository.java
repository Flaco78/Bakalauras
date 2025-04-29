package org.example.backend.repository;

import org.example.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByChildId(Long childId);
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.activity.provider.id = :providerId")
    List<Booking> findByProviderId(@Param("providerId") Long providerId);
}
