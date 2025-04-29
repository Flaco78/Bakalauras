package org.example.backend.service;

import org.example.backend.dto.BookingDTO;
import org.example.backend.enums.BookingStatus;
import org.example.backend.mapper.BookingMapper;
import org.example.backend.model.ActivityTimeSlot;
import org.example.backend.model.Booking;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.BookingRepository;
import org.example.backend.repository.ChildProfileRepository;
import org.example.backend.repository.TimeslotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ChildProfileRepository childProfileRepository;
    private final TimeslotRepository timeSlotRepository;

    public BookingService(BookingRepository bookingRepository, ChildProfileRepository childProfileRepository, TimeslotRepository timeSlotRepository) {
        this.bookingRepository = bookingRepository;
        this.childProfileRepository = childProfileRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public BookingDTO createBooking(Long childId, Long timeSlotId) {
        if (childId == null || timeSlotId == null) {
            throw new IllegalArgumentException("childId or timeSlotId cannot be null");
        }

        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        ActivityTimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (timeSlot.getCurrentParticipants() == null) {
            timeSlot.setCurrentParticipants(0);
        }

        if (timeSlot.getCurrentParticipants() >= timeSlot.getMaxParticipants()) {
            throw new IllegalStateException("This time slot is full!");
        }

        timeSlot.setCurrentParticipants(timeSlot.getCurrentParticipants() + 1);
        timeSlotRepository.save(timeSlot);

        Booking booking = new Booking();
        booking.setChild(child);
        booking.setTimeSlot(timeSlot);
        booking.setBookedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);

        booking = bookingRepository.save(booking);

        return BookingMapper.toDTO(booking);
    }

    public List<BookingDTO> getBookingsByChild(Long childId) {
        return bookingRepository.findByChildId(childId).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.ACTIVE)
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByProvider(Long providerId) {
        return bookingRepository.findByProviderId(providerId).stream()
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        ActivityTimeSlot timeSlot = booking.getTimeSlot();

        if (timeSlot.getCurrentParticipants() != null && timeSlot.getCurrentParticipants() > 0) {
            timeSlot.setCurrentParticipants(timeSlot.getCurrentParticipants() - 1);
            timeSlotRepository.save(timeSlot);
        }
    }
}
