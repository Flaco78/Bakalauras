package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByEmail(String email);
    boolean existsByEmail(String email);
    @NonNull Optional<Provider> findById(@NonNull Long id);
}
