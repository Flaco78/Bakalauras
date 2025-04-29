package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.ProviderRequest;
import org.example.backend.enums.ProviderStatus;
import org.example.backend.repository.ProviderRequestRepository;
import org.example.backend.service.ProviderRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/provider-request")
@RequiredArgsConstructor
public class ProviderRequestController {
    private final ProviderRequestRepository providerRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProviderRequestService providerRequestService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProvider(@RequestBody ProviderRequest request) {
        if (providerRequestRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("A provider with this email already exists.");
        }

        if (providerRequestRepository.findByEmailAndStatus(request.getEmail(), ProviderStatus.PENDING).isPresent()) {
            return ResponseEntity.badRequest().body("A pending registration request already exists for this email.");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setStatus(ProviderStatus.PENDING);
        providerRequestRepository.save(request);
        return ResponseEntity.ok("Registration request sent, waiting for approval.");
    }

    // Get all provider requests
    @GetMapping("/all")
    public ResponseEntity<List<ProviderRequest>> getAllProvider() {
        List<ProviderRequest> providerRequests = providerRequestService.getAllProviderRequests();
        return ResponseEntity.ok(providerRequests);
    }

    // ProviderRequestController.java
    @GetMapping
    public ResponseEntity<List<ProviderRequest>> getProviderRequestsByStatus(@RequestParam(required = false) ProviderStatus status) {
        List<ProviderRequest> providerRequests;

        if (status != null) {
            providerRequests = providerRequestService.getRequestsByStatus(status);
        } else {
            providerRequests = providerRequestService.getAllProviderRequests();
        }

        return ResponseEntity.ok(providerRequests);
    }

    // Get provider request by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProviderRequest> getProviderRequestById(@PathVariable Long id) {
        Optional<ProviderRequest> providerRequests = providerRequestService.getProviderRequestById(id);
        return providerRequests.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new provider request
    @PostMapping("/create")
    public ResponseEntity<ProviderRequest> createProviderRequest(@RequestBody ProviderRequest providerRequest) {
        try {
            ProviderRequest createdProviderRequest = providerRequestService.createProviderRequest(providerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProviderRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Update provider request
    @PutMapping("/{id}")
    public ResponseEntity<ProviderRequest> updateProviderRequest(@PathVariable Long id, @RequestBody ProviderRequest updatedProviderRequest) {
        Optional<ProviderRequest> providerRequest = providerRequestService.updateProviderRequest(id, updatedProviderRequest, updatedProviderRequest);
        return providerRequest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete provider requesst
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProviderRequest(@PathVariable Long id) {
        boolean deleted = providerRequestService.deleteProviderRequest(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
