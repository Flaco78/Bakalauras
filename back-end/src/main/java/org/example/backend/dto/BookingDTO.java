package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private Long childId;
    private String email;
    private Long timeSlotId;
    private Long activityId;
    private String activityTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String providerEmail;
    private String childName;
    private BookingStatus status;
}
