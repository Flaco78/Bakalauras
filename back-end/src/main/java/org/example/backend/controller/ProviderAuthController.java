package org.example.backend.controller;

import org.example.backend.model.Provider;
import org.example.backend.repository.ProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.example.backend.model.Role;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth/provider")
public class ProviderAuthController {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProviderRepository providerRepository;

    public ProviderAuthController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder, ProviderRepository providerRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCurrentProvider(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token missing or malformed");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);

        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        List<String> roles = provider.getRoles().stream()
                .map(Role::getName)
                .toList();

        return ResponseEntity.ok(Map.of(
                "id", provider.getId(),
                "email", provider.getEmail(),
                "name", provider.getName(),
                "roles", roles
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginProvider(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Provider provider = providerRepository.findByEmail(email).orElse(null);

        if (provider == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vartotojas nerastas");
        }

        if (!passwordEncoder.matches(password, provider.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neteisingas slaptažodis");
        }

        List<String> roles = provider.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtUtil.generateToken(provider.getEmail(), roles);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping
    public ResponseEntity<?> updateProvider(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody Map<String, String> updates) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or malformed");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        if (updates.containsKey("email")) {
            provider.setEmail(updates.get("email"));
        }

        if (updates.containsKey("password") && updates.get("password") != null && !updates.get("password").isEmpty()) {
            provider.setPassword(passwordEncoder.encode(updates.get("password")));
        }

        if (updates.containsKey("name")) {
            provider.setName(updates.get("name"));
        }

        providerRepository.save(provider);

        List<String> roles = provider.getRoles().stream()
                .map(Role::getName)
                .toList();

        String newToken = jwtUtil.generateToken(provider.getEmail(), roles);

        return ResponseEntity.ok(Map.of(
                "provider", Map.of(
                        "id", provider.getId(),
                        "email", provider.getEmail(),
                        "name", provider.getName(),
                        "roles", roles
                ),
                "token", newToken
        ));
    }
}
