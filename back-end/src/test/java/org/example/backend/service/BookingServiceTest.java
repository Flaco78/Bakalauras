package org.example.backend.service;

import org.example.backend.dto.BookingDTO;
import org.example.backend.enums.*;
import org.example.backend.model.*;
import org.example.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ChildProfileRepository childProfileRepository;
    @Mock
    private TimeslotRepository timeSlotRepository;
    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_successful_booking_creation() {
        Long childId = 1L;
        Long timeSlotId = 2L;

        ChildProfile child = new ChildProfile();
        child.setId(childId);

        ActivityTimeSlot timeSlot = getActivityTimeSlot(timeSlotId);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setChild(child);
        booking.setTimeSlot(timeSlot);
        booking.setBookedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);

        when(childProfileRepository.findById(childId)).thenReturn(Optional.of(child));
        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(any(ActivityTimeSlot.class))).thenReturn(timeSlot);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDTO result = bookingService.createBooking(childId, timeSlotId);

        assertNotNull(result);
        assertEquals(childId, result.getChildId());
        assertEquals(timeSlotId, result.getTimeSlotId());
        assertEquals(BookingStatus.ACTIVE, result.getStatus());

        verify(childProfileRepository).findById(childId);
        verify(timeSlotRepository).findById(timeSlotId);
        verify(timeSlotRepository).save(any(ActivityTimeSlot.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    private static ActivityTimeSlot getActivityTimeSlot(Long timeSlotId) {
        ActivityTimeSlot timeSlot = new ActivityTimeSlot();
        timeSlot.setId(timeSlotId);
        timeSlot.setStartDateTime(LocalDateTime.of(2024, 6, 1, 10, 0));
        timeSlot.setEndDateTime(LocalDateTime.of(2024, 6, 1, 12, 0));
        timeSlot.setMaxParticipants(5);
        timeSlot.setCurrentParticipants(2);

        Activity activity = new Activity();
        activity.setId(10L);

        Provider provider = new Provider();
        provider.setId(99L);
        provider.setEmail("provider@example.com");
        activity.setProvider(provider);

        timeSlot.setActivity(activity);
        return timeSlot;
    }

    @Test
    void test_getBookingsByChild_returns_active_bookings_only() {
        Long childId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.ACTIVE);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.CANCELLED);

        ChildProfile child = new ChildProfile();
        child.setId(1L);
        booking1.setChild(child);
        booking2.setChild(child);

        ActivityTimeSlot timeSlot = getActivityTimeSlot(2L);
        booking1.setTimeSlot(timeSlot);
        booking2.setTimeSlot(timeSlot);


        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findByChildId(childId)).thenReturn(bookings);

        List<BookingDTO> result = bookingService.getBookingsByChild(childId);

        assertEquals(1, result.size());
        assertEquals(BookingStatus.ACTIVE, result.get(0).getStatus());
        verify(bookingRepository).findByChildId(childId);
    }

    @Test
    void test_getBookingsByProvider_returns_all_bookings() {
        Long providerId = 99L;

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.ACTIVE);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.CANCELLED);

        ChildProfile child = new ChildProfile();
        child.setId(1L);
        booking1.setChild(child);
        booking2.setChild(child);

        ActivityTimeSlot timeSlot = getActivityTimeSlot(2L);
        booking1.setTimeSlot(timeSlot);
        booking2.setTimeSlot(timeSlot);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findByProviderId(providerId)).thenReturn(bookings);

        List<BookingDTO> result = bookingService.getBookingsByProvider(providerId);

        assertEquals(2, result.size());
        verify(bookingRepository).findByProviderId(providerId);
    }

    @Test
    void test_cancelBooking_marks_booking_cancelled_and_decrements_participants() {
        Long bookingId = 123L;

        ActivityTimeSlot timeSlot = getActivityTimeSlot(2L);
        timeSlot.setCurrentParticipants(3);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setTimeSlot(timeSlot);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(timeSlotRepository.save(any(ActivityTimeSlot.class))).thenReturn(timeSlot);

        bookingService.cancelBooking(bookingId);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(2, timeSlot.getCurrentParticipants());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void test_cancelBooking_when_already_cancelled_does_nothing() {
        Long bookingId = 123L;

        ActivityTimeSlot timeSlot = getActivityTimeSlot(2L);
        timeSlot.setCurrentParticipants(3);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setTimeSlot(timeSlot);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        // Should not save again
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(timeSlotRepository, never()).save(any(ActivityTimeSlot.class));
    }

    @Test
    void test_cancelBooking_when_booking_not_found_throws() {
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(bookingId));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }
}
