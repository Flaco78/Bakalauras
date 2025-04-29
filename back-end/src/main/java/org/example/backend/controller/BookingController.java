package org.example.backend.controller;

import org.example.backend.dto.BookingDTO;
import org.example.backend.service.BookingService;
import org.example.backend.service.ProviderService;
import org.example.backend.validation.BookingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ProviderService providerService;

    public BookingController(BookingService bookingService, ProviderService providerService) {
        this.bookingService = bookingService;
        this.providerService = providerService;
    }

    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingRequest request) {
        BookingDTO booking = bookingService.createBooking(request.getChildId(), request.getTimeSlotId());
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByChild(@PathVariable Long childId) {
        return ResponseEntity.ok(bookingService.getBookingsByChild(childId));
    }

    @GetMapping("/provider")
    public ResponseEntity<List<BookingDTO>> getBookingsForCurrentProvider(Authentication auth) {
        String email = auth.getName();
        Long providerId = providerService.findByEmail(email).getId();
        return ResponseEntity.ok(bookingService.getBookingsByProvider(providerId));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }
}
