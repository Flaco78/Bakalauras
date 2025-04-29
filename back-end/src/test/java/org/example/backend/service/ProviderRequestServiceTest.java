package org.example.backend.service;

import org.example.backend.enums.ProviderStatus;
import org.example.backend.model.ProviderRequest;
import org.example.backend.repository.ProviderRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderRequestServiceTest {

    private ProviderRequestRepository providerRequestRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ProviderRequestService service;

    @BeforeEach
    void setUp() {
        providerRequestRepository = mock(ProviderRequestRepository.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        service = new ProviderRequestService(providerRequestRepository, bCryptPasswordEncoder);
    }

    @Test
    void getAllProviderRequests_returnsAll() {
        List<ProviderRequest> requests = List.of(new ProviderRequest(), new ProviderRequest());
        when(providerRequestRepository.findAll()).thenReturn(requests);

        List<ProviderRequest> result = service.getAllProviderRequests();

        assertEquals(2, result.size());
        verify(providerRequestRepository).findAll();
    }

    @Test
    void getProviderRequestById_returnsOptional() {
        ProviderRequest req = new ProviderRequest();
        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));

        Optional<ProviderRequest> result = service.getProviderRequestById(1L);

        assertTrue(result.isPresent());
        assertEquals(req, result.get());
        verify(providerRequestRepository).findById(1L);
    }

    @Test
    void getRequestsByStatus_returnsFiltered() {
        List<ProviderRequest> requests = List.of(new ProviderRequest());
        when(providerRequestRepository.findAllByStatus(ProviderStatus.PENDING)).thenReturn(requests);

        List<ProviderRequest> result = service.getRequestsByStatus(ProviderStatus.PENDING);

        assertEquals(1, result.size());
        verify(providerRequestRepository).findAllByStatus(ProviderStatus.PENDING);
    }

    @Test
    void createProviderRequest_success() {
        ProviderRequest req = new ProviderRequest();
        req.setEmail("test@example.com");
        req.setPassword("plainpass");

        when(providerRequestRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("plainpass")).thenReturn("hashedpass");
        when(providerRequestRepository.save(any(ProviderRequest.class))).thenAnswer(i -> i.getArgument(0));

        ProviderRequest result = service.createProviderRequest(req);

        assertEquals("hashedpass", result.getPassword());
        verify(providerRequestRepository).save(req);
    }

    @Test
    void createProviderRequest_duplicateEmail_throws() {
        ProviderRequest req = new ProviderRequest();
        req.setEmail("test@example.com");
        req.setPassword("plainpass");

        when(providerRequestRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createProviderRequest(req));
        verify(providerRequestRepository, never()).save(any());
    }

    @Test
    void createProviderRequest_emptyPassword_throws() {
        ProviderRequest req = new ProviderRequest();
        req.setEmail("test@example.com");
        req.setPassword("");

        when(providerRequestRepository.existsByEmail("test@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.createProviderRequest(req));
        verify(providerRequestRepository, never()).save(any());
    }

    @Test
    void updateProviderRequest_success() {
        ProviderRequest existing = new ProviderRequest();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setPassword("oldpass");
        existing.setStatus(ProviderStatus.PENDING);

        ProviderRequest updated = new ProviderRequest();
        updated.setEmail("new@example.com");
        updated.setPassword("newpass");
        updated.setStatus(ProviderStatus.APPROVED);

        when(providerRequestRepository.existsById(1L)).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newpass")).thenReturn("hashednewpass");
        when(providerRequestRepository.save(any(ProviderRequest.class))).thenAnswer(i -> i.getArgument(0));

        Optional<ProviderRequest> result = service.updateProviderRequest(1L, existing, updated);

        assertTrue(result.isPresent());
        assertEquals("new@example.com", result.get().getEmail());
        assertEquals("hashednewpass", result.get().getPassword());
        assertEquals(ProviderStatus.APPROVED, result.get().getStatus());
        verify(providerRequestRepository).save(existing);
    }

    @Test
    void updateProviderRequest_notFound_returnsEmpty() {
        ProviderRequest existing = new ProviderRequest();
        ProviderRequest updated = new ProviderRequest();

        when(providerRequestRepository.existsById(1L)).thenReturn(false);

        Optional<ProviderRequest> result = service.updateProviderRequest(1L, existing, updated);

        assertTrue(result.isEmpty());
        verify(providerRequestRepository, never()).save(any());
    }

    @Test
    void deleteProviderRequest_success() {
        when(providerRequestRepository.existsById(1L)).thenReturn(true);

        boolean result = service.deleteProviderRequest(1L);

        assertTrue(result);
        verify(providerRequestRepository).deleteById(1L);
    }

    @Test
    void deleteProviderRequest_notFound() {
        when(providerRequestRepository.existsById(1L)).thenReturn(false);

        boolean result = service.deleteProviderRequest(1L);

        assertFalse(result);
        verify(providerRequestRepository, never()).deleteById(any());
    }
}
