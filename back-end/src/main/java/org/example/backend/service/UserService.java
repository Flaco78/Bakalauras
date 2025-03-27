package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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

        return userRepository.save(user);
    }

    // Update user
    public Optional<User> updateUser(Long userId, User user, User updatedUser) {
        if (userRepository.existsById(userId)) {
            user.setId(userId);
            user.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                String encryptedPassword = bCryptPasswordEncoder.encode(updatedUser.getPassword());
                user.setPassword(encryptedPassword);
            }

            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
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
