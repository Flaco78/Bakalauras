package org.example.backend.mapper;

import org.example.backend.dto.BookingDTO;
import org.example.backend.model.Booking;

import java.time.format.DateTimeFormatter;

public class BookingMapper {

    public static BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getChild().getId(),
                booking.getChild().getParent().getEmail(),
                booking.getTimeSlot().getId(),
                booking.getTimeSlot().getActivity().getId(),
                booking.getTimeSlot().getActivity().getTitle(),
                booking.getTimeSlot().getStartDateTime(),
                booking.getTimeSlot().getEndDateTime(),
                booking.getTimeSlot().getActivity().getProvider().getEmail(),
                booking.getChild().getName(),
                booking.getStatus()
        );
    }
}
