package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @NonNull Optional<User> findById(@NonNull Long id);
}