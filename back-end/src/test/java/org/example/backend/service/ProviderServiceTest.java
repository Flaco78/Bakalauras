package org.example.backend.service;

import org.example.backend.enums.ProviderStatus;
import org.example.backend.enums.ProviderType;
import org.example.backend.model.Provider;
import org.example.backend.model.ProviderRequest;
import org.example.backend.model.Role;
import org.example.backend.repository.ProviderRepository;
import org.example.backend.repository.ProviderRequestRepository;
import org.example.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderServiceTest {

    private ProviderRepository providerRepository;
    private ProviderRequestRepository providerRequestRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ProviderService service;

    @BeforeEach
    void setUp() {
        providerRepository = mock(ProviderRepository.class);
        providerRequestRepository = mock(ProviderRequestRepository.class);
        roleRepository = mock(RoleRepository.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        service = new ProviderService(providerRepository, providerRequestRepository, roleRepository, bCryptPasswordEncoder);
    }

    @Test
    void getProviderById_returnsOptional() {
        Provider provider = new Provider();
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));

        Optional<Provider> result = service.getProviderById(1L);

        assertTrue(result.isPresent());
        assertEquals(provider, result.get());
        verify(providerRepository).findById(1L);
    }

    @Test
    void findByEmail_returnsProvider() {
        Provider provider = new Provider();
        when(providerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(provider));

        Provider result = service.findByEmail("test@example.com");

        assertEquals(provider, result);
        verify(providerRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_notFound_throws() {
        when(providerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findByEmail("test@example.com"));
    }

    @Test
    void getAllProviders_returnsList() {
        List<Provider> providers = List.of(new Provider(), new Provider());
        when(providerRepository.findAll()).thenReturn(providers);

        List<Provider> result = service.getAllProviders();

        assertEquals(2, result.size());
        verify(providerRepository).findAll();
    }

    @Test
    void approveProviderRequest_success() {
        ProviderRequest req = new ProviderRequest();
        req.setId(1L);
        req.setStatus(ProviderStatus.PENDING);
        req.setProviderType(ProviderType.INDIVIDUAL);
        req.setName("Name");
        req.setEmail("email@example.com");
        req.setPhone("+37060000000");
        req.setWebsite("http://site.com");
        req.setDescription("desc");
        req.setPassword("plainpass");

        Role providerRole = new Role("PROVIDER");
        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));
        when(roleRepository.findByName("PROVIDER")).thenReturn(Optional.of(providerRole));
        when(bCryptPasswordEncoder.encode("plainpass")).thenReturn("hashedpass");

        service.approveProviderRequest(1L);

        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository).save(providerCaptor.capture());
        Provider savedProvider = providerCaptor.getValue();
        assertEquals("hashedpass", savedProvider.getPassword());
        assertEquals("email@example.com", savedProvider.getEmail());
        assertEquals(Set.of(providerRole), savedProvider.getRoles());

        assertEquals(ProviderStatus.APPROVED, req.getStatus());
        verify(providerRequestRepository).save(req);
    }

    @Test
    void approveProviderRequest_notPending_throws() {
        ProviderRequest req = new ProviderRequest();
        req.setId(1L);
        req.setStatus(ProviderStatus.APPROVED);

        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));

        assertThrows(IllegalStateException.class, () -> service.approveProviderRequest(1L));
    }

    @Test
    void approveProviderRequest_emptyPassword_throws() {
        ProviderRequest req = new ProviderRequest();
        req.setId(1L);
        req.setStatus(ProviderStatus.PENDING);
        req.setPassword("");

        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));

        assertThrows(IllegalArgumentException.class, () -> service.approveProviderRequest(1L));
    }

    @Test
    void declineProviderRequest_success() {
        Provider provider = new Provider();
        provider.setId(1L);
        ProviderRequest req = new ProviderRequest();
        req.setId(1L);
        req.setStatus(ProviderStatus.PENDING);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));

        service.declineProviderRequest(1L, "reason");

        assertEquals(ProviderStatus.REJECTED, req.getStatus());
        assertEquals("reason", req.getRejectionReason());
        verify(providerRepository).save(provider);
    }

    @Test
    void declineProviderRequest_notPending_throws() {
        Provider provider = new Provider();
        provider.setId(1L);
        ProviderRequest req = new ProviderRequest();
        req.setId(1L);
        req.setStatus(ProviderStatus.APPROVED);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(providerRequestRepository.findById(1L)).thenReturn(Optional.of(req));

        assertThrows(IllegalStateException.class, () -> service.declineProviderRequest(1L, "reason"));
    }

    @Test
    void createProvider_success() {
        Provider provider = new Provider();
        provider.setEmail("test@example.com");
        provider.setPassword("plainpass");

        Role providerRole = new Role("PROVIDER");
        when(providerRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("plainpass")).thenReturn("hashedpass");
        when(roleRepository.findByName("PROVIDER")).thenReturn(Optional.of(providerRole));
        when(providerRepository.save(any(Provider.class))).thenAnswer(i -> i.getArgument(0));

        Provider result = service.createProvider(provider);

        assertEquals("hashedpass", result.getPassword());
        assertEquals(Set.of(providerRole), result.getRoles());
        verify(providerRepository).save(provider);
    }

    @Test
    void createProvider_duplicateEmail_throws() {
        Provider provider = new Provider();
        provider.setEmail("test@example.com");
        provider.setPassword("plainpass");

        when(providerRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.createProvider(provider));
        verify(providerRepository, never()).save(any());
    }

    @Test
    void createProvider_emptyPassword_throws() {
        Provider provider = new Provider();
        provider.setEmail("test@example.com");
        provider.setPassword("");

        when(providerRepository.existsByEmail("test@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.createProvider(provider));
        verify(providerRepository, never()).save(any());
    }

    @Test
    void updateProvider_success() {
        Provider existing = new Provider();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setPassword("oldpass");
        existing.setRoles(new HashSet<>());

        Provider updated = new Provider();
        updated.setEmail("new@example.com");
        updated.setName("New Name");
        updated.setWebsite("http://new.com");
        updated.setProviderType(ProviderType.COMPANY);
        updated.setPhone("+37060000001");
        updated.setDescription("desc2");
        updated.setCompanyName("NewCo");
        updated.setCompanyCode("987654321");
        updated.setPassword("newpass");

        Role providerRole = new Role("PROVIDER");
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bCryptPasswordEncoder.encode("newpass")).thenReturn("hashednewpass");
        when(roleRepository.findByName("PROVIDER")).thenReturn(Optional.of(providerRole));
        when(providerRepository.save(any(Provider.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Provider> result = service.updateProvider(1L, updated);

        assertTrue(result.isPresent());
        Provider saved = result.get();
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("New Name", saved.getName());
        assertEquals("hashednewpass", saved.getPassword());
        assertEquals(Set.of(providerRole), saved.getRoles());
        verify(providerRepository).save(existing);
    }

    @Test
    void updateProvider_notFound_returnsEmpty() {
        Provider updated = new Provider();
        when(providerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Provider> result = service.updateProvider(1L, updated);

        assertTrue(result.isEmpty());
        verify(providerRepository, never()).save(any());
    }

    @Test
    void deleteProvider_success() {
        when(providerRepository.existsById(1L)).thenReturn(true);

        boolean result = service.deleteProvider(1L);

        assertTrue(result);
        verify(providerRepository).deleteById(1L);
    }

    @Test
    void deleteProvider_notFound_returnsFalse() {
        when(providerRepository.existsById(1L)).thenReturn(false);

        boolean result = service.deleteProvider(1L);

        assertFalse(result);
        verify(providerRepository, never()).deleteById(any());
    }
}