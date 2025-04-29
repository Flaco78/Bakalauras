package org.example.backend.controller;

import jakarta.validation.Valid;
import org.example.backend.dto.ChildProfileDTO;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.RoleRepository;
import org.example.backend.model.User;
import org.example.backend.response.UserResponse;
import org.example.backend.security.JwtUtil;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.ChildProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.example.backend.model.Role;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChildProfileService childProfileService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, ChildProfileService childProfileService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.childProfileService = childProfileService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Šis el. paštas jau naudojamas"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

//        Role userRole = roleRepository.findByName("USER")
//                        .orElseThrow(() -> new RuntimeException("Tokia rolė neegzistuoja"));
//        user.setRoles(Set.of(userRole));

        Optional<Role> userRoleOpt = roleRepository.findByName("USER");
        if (userRoleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Tokia rolė neegzistuoja"));
        }
        user.setRoles(Set.of(userRoleOpt.get()));

        userRepository.save(user);
        return ResponseEntity.ok("Vartotojo registracija sėkminga");
    }

    // Prisijungimas ir JWT gavimas
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vartotojas nerastas");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neteisingas slaptažodis");
        }

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtUtil.generateToken(email, roleNames);
        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or malformed");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User %s not found", email)));

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getAddress() ,roleNames);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody Map<String, String> updates) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or malformed");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User %s not found", email)));

        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        if (updates.containsKey("password") && updates.get("password") != null && !updates.get("password").isEmpty()) {
            user.setPassword(passwordEncoder.encode(updates.get("password")));
        }

        userRepository.save(user);

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String newToken = jwtUtil.generateToken(user.getEmail(), roleNames);

        return ResponseEntity.ok(Map.of(
                "user", user,
                "token", newToken
        ));
    }


    @GetMapping("/user/child-profiles")
    public ResponseEntity<List<ChildProfileDTO>> getChildProfilesByAuthenticatedUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User %s not found", email)));

        List<ChildProfile> childProfiles = childProfileService.getChildProfilesByParentId(user.getId());
        List<ChildProfileDTO> dtos = childProfiles.stream()
                .map(org.example.backend.mapper.ChildProfileMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}