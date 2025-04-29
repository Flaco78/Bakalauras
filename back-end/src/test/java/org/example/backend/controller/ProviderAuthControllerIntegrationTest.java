
package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.model.Provider;
import org.example.backend.model.Role;
import org.example.backend.repository.ProviderRepository;
import org.example.backend.repository.RoleRepository;
import org.example.backend.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProviderAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider provider;
    private String rawPassword = "providerpass";
    private String token;
    private Long testProviderId;

    @BeforeEach
    void setUp() {
        Role providerRole = roleRepository.findByName("PROVIDER")
                .orElseThrow(() -> new RuntimeException("PROVIDER role not found in DB"));


        provider = new Provider();
        provider.setEmail("provider@example.com");
        provider.setName("Test Provider");
        provider.setDescription("Test Provider");
        provider.setPhone("+37060186472");
        provider.setPassword(passwordEncoder.encode(rawPassword));
        provider.setRoles(Set.of(providerRole));
        provider = providerRepository.save(provider);
        testProviderId = provider.getId();

        token = jwtUtil.generateToken(provider.getEmail(), provider.getRoles().stream().map(Role::getName).toList());
    }

    @AfterEach
    void tearDown() {
        if (testProviderId != null && providerRepository.existsById(testProviderId)) {
            providerRepository.deleteById(testProviderId);
        }
    }

    @Test
    void loginProvider_success() throws Exception {
        mockMvc.perform(post("/api/auth/provider/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", provider.getEmail(), "password", rawPassword)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    void loginProvider_wrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/provider/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", provider.getEmail(), "password", "wrongpass")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Neteisingas slaptažodis")));
    }

    @Test
    void loginProvider_userNotFound() throws Exception {
        mockMvc.perform(post("/api/auth/provider/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("email", "notfound@example.com", "password", "any")
                        )))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Vartotojas nerastas")));
    }

    @Test
    void getCurrentProvider_success() throws Exception {
        mockMvc.perform(get("/api/auth/provider")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(provider.getEmail())))
                .andExpect(jsonPath("$.name", is(provider.getName())))
                .andExpect(jsonPath("$.roles", hasItem("PROVIDER")));
    }

    @Test
    void getCurrentProvider_invalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/provider")
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid or expired token")));
    }

    @Test
    void updateProvider_success() throws Exception {
        String newName = "Updated Provider";
        String newEmail = "updated@example.com";
        String newPassword = "newpass123";

        mockMvc.perform(put("/api/auth/provider")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of(
                                        "name", newName,
                                        "email", newEmail,
                                        "password", newPassword
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider.name", is(newName)))
                .andExpect(jsonPath("$.provider.email", is(newEmail)))
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    void updateProvider_invalidToken() throws Exception {
        mockMvc.perform(put("/api/auth/provider")
                        .header("Authorization", "Bearer invalidtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of("name", "ShouldNotUpdate")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid or expired token")));
    }
}
