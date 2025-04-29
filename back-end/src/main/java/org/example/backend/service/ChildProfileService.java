package org.example.backend.service;

import org.example.backend.model.ChildProfile;
import org.example.backend.model.User;
import org.example.backend.repository.ChildProfileRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChildProfileService {
    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChildProfileService(ChildProfileRepository childProfileRepository, UserRepository userRepository) {
        this.childProfileRepository = childProfileRepository;
        this.userRepository = userRepository;
    }

    //all
    public List<ChildProfile> getAllChildProfiles() {
        return childProfileRepository.findAll();
    }

    //get by id
    public Optional<ChildProfile> getChildProfileById(Long id) {
        return childProfileRepository.findById(id);
    }

    //get by parent id
    public List<ChildProfile> getChildProfilesByParentId(Long parentId) {
        return childProfileRepository.findByParentId(parentId);
    }

    //create childprof
    public ChildProfile createChildProfile(ChildProfile childProfile, String userEmail) {
        User parent = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        childProfile.setParent(parent); // Automatiškai priskiriame prisijungusį vartotoją kaip tėvą
        return childProfileRepository.save(childProfile);
    }

    //update
    public Optional<ChildProfile> updateChildProfile(Long id, ChildProfile updatedProfile) {
        return childProfileRepository.findById(id).map(existingProfile -> {
            existingProfile.setName(updatedProfile.getName());
            existingProfile.setBirthDate(updatedProfile.getBirthDate());
            existingProfile.setGender(updatedProfile.getGender());
            existingProfile.setMaxActivityDuration(updatedProfile.getMaxActivityDuration());
            existingProfile.setPreferredDeliveryMethod(updatedProfile.getPreferredDeliveryMethod());
            existingProfile.setInterests(updatedProfile.getInterests());
            return childProfileRepository.save(existingProfile);
        });
    }


    //kill it muhahaha
    public boolean deleteChildProfile(Long id) {
        if (childProfileRepository.existsById(id)) {
            childProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }
}