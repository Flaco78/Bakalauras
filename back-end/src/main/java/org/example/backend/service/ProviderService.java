package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Provider;
import org.example.backend.model.ProviderRequest;
import org.example.backend.model.ProviderStatus;
import org.example.backend.repository.ProviderRepository;
import org.example.backend.repository.ProviderRequestRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderRequestRepository providerRequestRepository;
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
        provider.setName(providerRequest.getName());
        provider.setEmail(providerRequest.getEmail());
        provider.setPhone(providerRequest.getPhone());
        provider.setWebsite(providerRequest.getWebsite());
        provider.setProviderType(providerRequest.getProviderType());

        if (providerRequest.getPassword() != null && !providerRequest.getPassword().isEmpty()) {
            provider.setPassword(bCryptPasswordEncoder.encode(providerRequest.getPassword()));
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        providerRepository.save(provider);
        providerRequest.setStatus(ProviderStatus.APPROVED);
        providerRequestRepository.save(providerRequest);

    }

    public Provider createProvider(Provider provider) {
        if (providerRepository.existsByEmail(provider.getEmail())) {
            throw new IllegalArgumentException("Provider with this email already exists");
        }

        if (provider.getPassword() != null && !provider.getPassword().isEmpty()) {
            String encryptedPassword = bCryptPasswordEncoder.encode(provider.getPassword());
            provider.setPassword(encryptedPassword);
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        return providerRepository.save(provider);
    }

    public Optional<Provider> updateProvider(Long id, Provider provider, Provider updatedProvider) {
        if (providerRepository.existsById(id)) {
            provider.setId(id);
            provider.setEmail(updatedProvider.getEmail());
            provider.setName(updatedProvider.getName());
            provider.setWebsite(updatedProvider.getWebsite());
            provider.setProviderType(updatedProvider.getProviderType());
            provider.setPhone(updatedProvider.getPhone());
            if (updatedProvider.getPassword() != null && !updatedProvider.getPassword().isEmpty()) {
                String encryptedPassword = bCryptPasswordEncoder.encode(updatedProvider.getPassword());
                provider.setPassword(encryptedPassword);
            }

            return Optional.of(providerRepository.save(provider));
        }
        return Optional.empty();
    }

    public boolean deleteProvider(Long id) {
        if (providerRepository.existsById(id)) {
            providerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
