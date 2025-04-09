package org.example.backend.response;

import java.util.List;

public record UserResponse(Long id, String email, String address, List<String> roles) {}
