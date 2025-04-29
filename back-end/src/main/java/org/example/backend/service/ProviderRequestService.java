package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.ProviderRequest;
import org.example.backend.enums.ProviderStatus;
import org.example.backend.repository.ProviderRequestRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderRequestService {
    private final ProviderRequestRepository providerRequestRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Get all provider requests
    public List<ProviderRequest> getAllProviderRequests() {
        return providerRequestRepository.findAll();
    }

    // Get provider request by ID
    public Optional<ProviderRequest> getProviderRequestById(Long providerRequestId) {
        return providerRequestRepository.findById(providerRequestId);
    }

    // ProviderRequestService.java
    public List<ProviderRequest> getRequestsByStatus(ProviderStatus status) {
        return providerRequestRepository.findAllByStatus(status);
    }

    // Create new provider request
    public ProviderRequest createProviderRequest(ProviderRequest providerRequest) {
        if (providerRequestRepository.existsByEmail(providerRequest.getEmail())) {
            throw new IllegalArgumentException("Provider request with this email already exists");
        }

        if (providerRequest.getPassword() != null && !providerRequest.getPassword().isEmpty()) {
            String encryptedPassword = bCryptPasswordEncoder.encode(providerRequest.getPassword());
            providerRequest.setPassword(encryptedPassword);
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        return providerRequestRepository.save(providerRequest);
    }

    // Update provider request
    public Optional<ProviderRequest> updateProviderRequest(Long requestId, ProviderRequest providerRequest, ProviderRequest updatedProviderRequest) {
        if (providerRequestRepository.existsById(requestId)) {
            providerRequest.setId(requestId);
            providerRequest.setEmail(updatedProviderRequest.getEmail());
            providerRequest.setStatus(updatedProviderRequest.getStatus());
            providerRequest.setPhone(updatedProviderRequest.getPhone());
            providerRequest.setWebsite(updatedProviderRequest.getWebsite());
            providerRequest.setName(updatedProviderRequest.getName());
            providerRequest.setRejectionReason(updatedProviderRequest.getRejectionReason());
            providerRequest.setProviderType(updatedProviderRequest.getProviderType());
            if (updatedProviderRequest.getPassword() != null && !updatedProviderRequest.getPassword().isEmpty()) {
                String encryptedPassword = bCryptPasswordEncoder.encode(updatedProviderRequest.getPassword());
                providerRequest.setPassword(encryptedPassword);
            }

            return Optional.of(providerRequestRepository.save(providerRequest));
        }
        return Optional.empty();
    }

    //Delete provider request
    public boolean deleteProviderRequest(Long id) {
        if (providerRequestRepository.existsById(id)) {
            providerRequestRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
