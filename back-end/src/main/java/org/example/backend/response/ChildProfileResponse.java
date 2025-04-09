package org.example.backend.response;

import java.time.LocalDate;
import java.time.Period;

public record ChildProfileResponse(
        Long id,
        String name,
        LocalDate birthDate,
        String gender
) {
    public int age() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}