package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildProfileDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private int maxActivityDuration;
    private DeliveryMethod preferredDeliveryMethod;
    private Set<ActivityCategory> interests;
}