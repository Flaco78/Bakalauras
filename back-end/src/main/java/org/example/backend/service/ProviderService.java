package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.enums.ProviderStatus;
import org.example.backend.enums.ProviderType;
import org.example.backend.model.*;
import org.example.backend.repository.ProviderRepository;
import org.example.backend.repository.ProviderRequestRepository;
import org.example.backend.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderRequestRepository providerRequestRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<Provider> getProviderById(Long id) {
        return providerRepository.findById(id);
    }

    public Provider findByEmail(String email) {
        return providerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Provider not found."));
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public void approveProviderRequest(Long requestId) {
        ProviderRequest providerRequest = providerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Provider request not found"));

        if (providerRequest.getStatus() != ProviderStatus.PENDING) {
            throw new IllegalStateException("Provider request is not in pending state.");
        }

        Provider provider = new Provider();
        if (providerRequest.getProviderType() == ProviderType.INDIVIDUAL) {
            provider.setName(providerRequest.getName());
        }
        provider.setEmail(providerRequest.getEmail());
        provider.setPhone(providerRequest.getPhone());
        provider.setWebsite(providerRequest.getWebsite());
        provider.setProviderType(providerRequest.getProviderType());
        provider.setDescription(providerRequest.getDescription());
        if (providerRequest.getProviderType() == ProviderType.COMPANY) {
            provider.setCompanyName(providerRequest.getCompanyName());
            provider.setCompanyCode(providerRequest.getCompanyCode());
        }

        if (providerRequest.getPassword() != null && !providerRequest.getPassword().isEmpty()) {
            provider.setPassword(bCryptPasswordEncoder.encode(providerRequest.getPassword()));
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (provider.getRoles() == null || provider.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("PROVIDER")
                    .orElseGet(() -> roleRepository.save(new Role("PROVIDER")));
            provider.setRoles(Set.of(userRole));
        }


        providerRepository.save(provider);
        providerRequest.setStatus(ProviderStatus.APPROVED);
        providerRequestRepository.save(providerRequest);

    }

    public void declineProviderRequest(Long requestId, String reason) {
        Provider provider = providerRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        ProviderRequest providerRequest = providerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Provider request not found"));

        if (providerRequest.getStatus() != ProviderStatus.PENDING) {
            throw new IllegalStateException("Provider request is not in pending state.");
        }

        providerRequest.setStatus(ProviderStatus.REJECTED);
        providerRequest.setRejectionReason(reason);
        providerRepository.save(provider);
    }

    public Provider createProvider(Provider provider) {
        if (providerRepository.existsByEmail(provider.getEmail())) {
            throw new IllegalArgumentException("Provider with this email already exists");
        }

        if (provider.getPassword() == null || provider.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String encryptedPassword = bCryptPasswordEncoder.encode(provider.getPassword());
        provider.setPassword(encryptedPassword);

        Role providerRole = roleRepository.findByName("PROVIDER")
                .orElseThrow(() -> new RuntimeException("Role PROVIDER not found in DB"));
        provider.setRoles(Set.of(providerRole));

        return providerRepository.save(provider);
    }

    public Optional<Provider> updateProvider(Long id, Provider updatedProvider) {
        return providerRepository.findById(id).map(provider -> {
            provider.setEmail(updatedProvider.getEmail());
            provider.setName(updatedProvider.getName());
            provider.setWebsite(updatedProvider.getWebsite());
            provider.setProviderType(updatedProvider.getProviderType());
            provider.setPhone(updatedProvider.getPhone());
            provider.setDescription(updatedProvider.getDescription());
            provider.setCompanyName(updatedProvider.getCompanyName());
            provider.setCompanyCode(updatedProvider.getCompanyCode());

            if (updatedProvider.getPassword() != null && !updatedProvider.getPassword().isEmpty()) {
                String encryptedPassword = bCryptPasswordEncoder.encode(updatedProvider.getPassword());
                provider.setPassword(encryptedPassword);
            }
            Role providerRole = roleRepository.findByName("PROVIDER")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            provider.setRoles(new HashSet<>(Collections.singletonList(providerRole)));

            return providerRepository.save(provider);
        });
    }

    public boolean deleteProvider(Long id) {
        if (providerRepository.existsById(id)) {
            providerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
