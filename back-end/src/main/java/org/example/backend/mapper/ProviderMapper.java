package org.example.backend.mapper;

import org.example.backend.dto.ProviderDTO;
import org.example.backend.model.Provider;

public class ProviderMapper {

    private ProviderMapper() {

    }

    public static ProviderDTO toDto(Provider provider) {
        if (provider == null) return null;
        return new ProviderDTO(
                provider.getId(),
                provider.getName(),
                provider.getEmail(),
                provider.getPhone(),
                provider.getProviderType(),
                provider.getWebsite(),
                provider.getDescription(),
                provider.getCompanyCode(),
                provider.getCompanyName()
        );
    }

    public static Provider toEntity(ProviderDTO dto) {
        Provider provider = new Provider();
        provider.setId(dto.getId());
        provider.setName(dto.getName());
        provider.setEmail(dto.getEmail());
        provider.setPhone(dto.getPhone());
        provider.setProviderType(dto.getProviderType());
        provider.setWebsite(dto.getWebsite());
        provider.setDescription(dto.getDescription());
        provider.setCompanyCode(dto.getCompanyCode());
        provider.setCompanyName(dto.getCompanyName());
        return provider;
    }
}