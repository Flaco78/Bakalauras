package org.example.backend.controller;

import org.example.backend.enums.ActivityCategory;
import org.example.backend.model.ChildProfile;
import org.example.backend.repository.ChildProfileRepository;
import org.example.backend.service.ChildProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/child-profiles")
public class ChildProfileController {

    private final ChildProfileService childProfileService;
    private final ChildProfileRepository childProfileRepository;

    @Autowired
    public ChildProfileController(ChildProfileService childProfileService, ChildProfileRepository childProfileRepository) {
        this.childProfileService = childProfileService;
        this.childProfileRepository = childProfileRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChildProfile>> getAllChildProfiles() {
        return ResponseEntity.ok(childProfileService.getAllChildProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChildProfile> getChildProfileById(@PathVariable Long id) {
        return childProfileService.getChildProfileById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<ChildProfile>> getChildProfilesByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(childProfileService.getChildProfilesByParentId(parentId));
    }

    @PostMapping("/create")
    public ResponseEntity<ChildProfile> createChildProfile(@RequestBody ChildProfile childProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = authentication.getName();  // Paimame vartotojo el. paštą iš autentifikacijos
        ChildProfile createdProfile = childProfileService.createChildProfile(childProfile, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChildProfile> updateChildProfile(@PathVariable Long id, @RequestBody ChildProfile updatedProfile) {
        return childProfileService.updateChildProfile(id, updatedProfile)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChildProfile(@PathVariable Long id) {
        return childProfileService.deleteChildProfile(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/children/{id}/interests")
    public ResponseEntity<Set<ActivityCategory>> getChildInterests(@PathVariable Long id) {
        ChildProfile child = childProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(child.getInterests());
    }

    @PutMapping("/children/{id}/interests")
    public ResponseEntity<Void> updateChildInterests(@PathVariable Long id, @RequestBody Set<ActivityCategory> interests) {
        ChildProfile child = childProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        child.setInterests(interests);
        childProfileRepository.save(child);

        return ResponseEntity.ok().build();
    }
}