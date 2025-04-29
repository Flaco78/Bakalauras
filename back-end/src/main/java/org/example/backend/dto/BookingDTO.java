package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.BookingStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    private Long childId;
    private Long timeSlotId;
    private Long activityId;
    private String activityTitle;
    private String startDateTime;
    private String endDateTime;
    private String providerEmail;
    private String childName;
    private BookingStatus status;
}
