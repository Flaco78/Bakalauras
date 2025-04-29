package org.example.backend.service;

import org.example.backend.model.ChildProfile;
import org.example.backend.model.User;
import org.example.backend.repository.ChildProfileRepository;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChildProfileServiceTest {

    @Mock
    private ChildProfileRepository childProfileRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChildProfileService childProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllChildProfiles() {
        List<ChildProfile> profiles = Arrays.asList(new ChildProfile(), new ChildProfile());
        when(childProfileRepository.findAll()).thenReturn(profiles);

        List<ChildProfile> result = childProfileService.getAllChildProfiles();

        assertEquals(2, result.size());
        verify(childProfileRepository).findAll();
    }

    @Test
    void testGetChildProfileById_found() {
        ChildProfile profile = new ChildProfile();
        profile.setId(1L);
        when(childProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Optional<ChildProfile> result = childProfileService.getChildProfileById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(childProfileRepository).findById(1L);
    }

    @Test
    void testGetChildProfileById_notFound() {
        when(childProfileRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<ChildProfile> result = childProfileService.getChildProfileById(2L);

        assertFalse(result.isPresent());
        verify(childProfileRepository).findById(2L);
    }

    @Test
    void testGetChildProfilesByParentId() {
        List<ChildProfile> profiles = Arrays.asList(new ChildProfile(), new ChildProfile());
        when(childProfileRepository.findByParentId(10L)).thenReturn(profiles);

        List<ChildProfile> result = childProfileService.getChildProfilesByParentId(10L);

        assertEquals(2, result.size());
        verify(childProfileRepository).findByParentId(10L);
    }

    @Test
    void testCreateChildProfile_success() {
        User parent = new User();
        parent.setId(5L);
        parent.setEmail("parent@example.com");

        ChildProfile profile = new ChildProfile();
        profile.setName("Child");
        profile.setBirthDate(LocalDate.of(2015, 1, 1));

        when(userRepository.findByEmail("parent@example.com")).thenReturn(Optional.of(parent));
        when(childProfileRepository.save(any(ChildProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        ChildProfile result = childProfileService.createChildProfile(profile, "parent@example.com");

        assertEquals(parent, result.getParent());
        assertEquals("Child", result.getName());
        verify(userRepository).findByEmail("parent@example.com");
        verify(childProfileRepository).save(profile);
    }

    @Test
    void testCreateChildProfile_userNotFound() {
        ChildProfile profile = new ChildProfile();
        when(userRepository.findByEmail("noone@example.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> childProfileService.createChildProfile(profile, "noone@example.com"));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository).findByEmail("noone@example.com");
        verify(childProfileRepository, never()).save(any());
    }

    @Test
    void testUpdateChildProfile_found() {
        ChildProfile existing = new ChildProfile();
        existing.setId(1L);
        existing.setName("Old");
        existing.setBirthDate(LocalDate.of(2015, 1, 1));

        ChildProfile updated = new ChildProfile();
        updated.setName("New");
        updated.setBirthDate(LocalDate.of(2016, 2, 2));

        when(childProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(childProfileRepository.save(any(ChildProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<ChildProfile> result = childProfileService.updateChildProfile(1L, updated);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getName());
        assertEquals(LocalDate.of(2016, 2, 2), result.get().getBirthDate());
        verify(childProfileRepository).findById(1L);
        verify(childProfileRepository).save(existing);
    }

    @Test
    void testUpdateChildProfile_notFound() {
        ChildProfile updated = new ChildProfile();
        when(childProfileRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<ChildProfile> result = childProfileService.updateChildProfile(2L, updated);

        assertFalse(result.isPresent());
        verify(childProfileRepository).findById(2L);
        verify(childProfileRepository, never()).save(any());
    }

    @Test
    void testDeleteChildProfile_exists() {
        when(childProfileRepository.existsById(1L)).thenReturn(true);

        boolean result = childProfileService.deleteChildProfile(1L);

        assertTrue(result);
        verify(childProfileRepository).existsById(1L);
        verify(childProfileRepository).deleteById(1L);
    }

    @Test
    void testDeleteChildProfile_notExists() {
        when(childProfileRepository.existsById(2L)).thenReturn(false);

        boolean result = childProfileService.deleteChildProfile(2L);

        assertFalse(result);
        verify(childProfileRepository).existsById(2L);
        verify(childProfileRepository, never()).deleteById(any());
    }
}
