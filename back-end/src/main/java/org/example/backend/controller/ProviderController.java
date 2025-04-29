package org.example.backend.controller;

import org.example.backend.dto.ProviderDTO;
import org.example.backend.mapper.ProviderMapper;
import org.example.backend.model.Provider;
import org.example.backend.service.ProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PatchMapping("/approve/{requestId}")
    public ResponseEntity<?> approveProvider(@PathVariable Long requestId) {
        try {
            providerService.approveProviderRequest(requestId);
            return ResponseEntity.ok("Provider approved and fully registered.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/decline/{requestId}")
    public ResponseEntity<Void> declineProvider(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> body
    ) {
        String reason = body.get("reason");
        providerService.declineProviderRequest(requestId, reason);
        return ResponseEntity.ok().build();
    }

    // Get all providers
    @GetMapping("/all")
    public ResponseEntity<List<ProviderDTO>> getAllProviders() {
        return ResponseEntity.ok(
                providerService.getAllProviders().stream()
                        .map(ProviderMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    // Get provider by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDTO> getProviderById(@PathVariable Long id) {
        return providerService.getProviderById(id)
                .map(ProviderMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new provider
    @PostMapping("/create")
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        try {
            Provider createdProvider = providerService.createProvider(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Update provider
    @PutMapping("/{id}")
    public ResponseEntity<ProviderDTO> updateProvider(@PathVariable Long id, @RequestBody ProviderDTO providerDTO) {
        var updatedProvider = providerService.updateProvider(id, ProviderMapper.toEntity(providerDTO));
        return updatedProvider
                .map(ProviderMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete provider
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        boolean deleted = providerService.deleteProvider(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
