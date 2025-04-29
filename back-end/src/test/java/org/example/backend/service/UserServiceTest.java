package org.example.backend.service;

import org.example.backend.model.Role;
import org.example.backend.model.User;
import org.example.backend.repository.RoleRepository;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        userService = new UserService(userRepository, roleRepository, bCryptPasswordEncoder);
    }

    @Test
    void getAllUsers_returnsList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_returnsOptional() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_successWithDefaultRole() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainpass");
        user.setRoles(null);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("plainpass")).thenReturn("hashedpass");
        Role userRole = new Role(1L, "USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(user);

        assertEquals("hashedpass", result.getPassword());
        assertEquals(Set.of(userRole), result.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_successWithProvidedRoles() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainpass");
        Role adminRole = new Role(2L, "ADMIN");
        user.setRoles(Set.of(adminRole));

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("plainpass")).thenReturn("hashedpass");
        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createUser(user);

        assertEquals("hashedpass", result.getPassword());
        assertEquals(Set.of(adminRole), result.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_duplicateEmail_throws() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainpass");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_emptyPassword_throws() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_successWithDefaultRole() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setAddress("Old Address");
        existing.setPassword("oldpass");
        existing.setRoles(new HashSet<>());

        User updated = new User();
        updated.setEmail("new@example.com");
        updated.setAddress("New Address");
        updated.setPassword("newpass");
        updated.setRoles(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bCryptPasswordEncoder.encode("newpass")).thenReturn("hashednewpass");
        Role userRole = new Role(1L, "USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Optional<User> result = userService.updateUser(1L, updated);

        assertTrue(result.isPresent());
        User saved = result.get();
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("New Address", saved.getAddress());
        assertEquals("hashednewpass", saved.getPassword());
        assertEquals(Set.of(userRole), saved.getRoles());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_successWithProvidedRoles() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setAddress("Old Address");
        existing.setPassword("oldpass");
        existing.setRoles(new HashSet<>());

        User updated = new User();
        updated.setEmail("new@example.com");
        updated.setAddress("New Address");
        updated.setPassword("newpass");
        Role adminRole = new Role(2L, "ADMIN");
        updated.setRoles(Set.of(adminRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bCryptPasswordEncoder.encode("newpass")).thenReturn("hashednewpass");
        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Optional<User> result = userService.updateUser(1L, updated);

        assertTrue(result.isPresent());
        User saved = result.get();
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("New Address", saved.getAddress());
        assertEquals("hashednewpass", saved.getPassword());
        assertEquals(Set.of(adminRole), saved.getRoles());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_notFound_returnsEmpty() {
        User updated = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(1L, updated);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound_returnsFalse() {
        when(userRepository.existsById(1L)).thenReturn(false);

        boolean result = userService.deleteUser(1L);

        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }
}
