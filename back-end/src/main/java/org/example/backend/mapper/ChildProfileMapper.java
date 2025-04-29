package org.example.backend.mapper;

import org.example.backend.dto.ChildProfileDTO;
import org.example.backend.model.ChildProfile;

public class ChildProfileMapper {
    public static ChildProfileDTO toDTO(ChildProfile childProfile) {
        ChildProfileDTO dto = new ChildProfileDTO();
        dto.setId(childProfile.getId());
        dto.setName(childProfile.getName());
        dto.setBirthDate(childProfile.getBirthDate());
        dto.setGender(childProfile.getGender());
        dto.setMaxActivityDuration(childProfile.getMaxActivityDuration());
        dto.setPreferredDeliveryMethod(childProfile.getPreferredDeliveryMethod());
        dto.setInterests(childProfile.getInterests());
        return dto;
    }
}