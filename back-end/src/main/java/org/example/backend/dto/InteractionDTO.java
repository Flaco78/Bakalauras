package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.InteractionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionDTO {
    private Long childId;
    private Long activityId;
    private InteractionType interactionType;
}

