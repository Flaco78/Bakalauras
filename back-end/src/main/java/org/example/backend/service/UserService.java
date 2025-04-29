package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Role;
import org.example.backend.repository.RoleRepository;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Create new user
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
        } else {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role newRole = new Role("USER");
                        return roleRepository.save(newRole);
                    });
            user.setRoles(Set.of(userRole));
        } else {
            Set<Role> validRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Optional<Role> existingRole = roleRepository.findById(role.getId());
                existingRole.ifPresent(validRoles::add);
            }
            user.setRoles(validRoles);
        }

        return userRepository.save(user);
    }

    // Update user
    public Optional<User> updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setAddress(updatedUser.getAddress());

            // Password handling: only update if provided
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                String encryptedPassword = bCryptPasswordEncoder.encode(updatedUser.getPassword());
                user.setPassword(encryptedPassword);
            }

            if (updatedUser.getRoles() == null || updatedUser.getRoles().isEmpty()) {
                Role userRole = roleRepository.findByName("USER")
                        .orElseGet(() -> {
                            Role newRole = new Role("USER");
                            return roleRepository.save(newRole); // Save new role if not found
                        });
                user.setRoles(Set.of(userRole));  // Assign default role if none is provided
            } else {
                // Validate roles by ID before updating
                Set<Role> validRoles = new HashSet<>();
                for (Role role : updatedUser.getRoles()) {
                    Optional<Role> existingRole = roleRepository.findById(role.getId());
                    existingRole.ifPresent(validRoles::add);
                }
                user.setRoles(validRoles);  // Update the user's roles with validated ones
            }

            return userRepository.save(user);  // Save the updated user
        });
    }

    // Delete user
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}
