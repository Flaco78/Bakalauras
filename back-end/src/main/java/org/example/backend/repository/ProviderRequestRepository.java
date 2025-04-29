package org.example.backend.repository;

import org.example.backend.model.ProviderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.backend.enums.ProviderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRequestRepository extends JpaRepository<ProviderRequest, Long> {
    List<ProviderRequest> findByStatus(ProviderStatus status);
    Optional<ProviderRequest> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<ProviderRequest> findByEmailAndStatus(String email, ProviderStatus status);
    List<ProviderRequest> findAllByStatus(ProviderStatus status);
}
