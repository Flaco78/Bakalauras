package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Provider;
import org.example.backend.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.example.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/auth/provider")
@RequiredArgsConstructor
public class ProviderAuthController {
    private final ProviderService providerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginProvider(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Provider provider = providerService.findByEmail(email);
        if (!passwordEncoder.matches(password, provider.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }


        String token = jwtUtil.generateToken(provider.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
