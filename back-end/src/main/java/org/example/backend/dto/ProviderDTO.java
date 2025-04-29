package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.ProviderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private ProviderType providerType;
    private String website;
    private String description;
    private String companyCode;
    private String companyName;
}
