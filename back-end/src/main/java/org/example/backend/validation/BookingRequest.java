package org.example.backend.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private Long childId;
    private Long timeSlotId;
}
