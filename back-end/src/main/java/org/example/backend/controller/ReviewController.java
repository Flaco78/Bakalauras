package org.example.backend.controller;

import org.example.backend.dto.ReviewDTO;
import org.example.backend.mapper.ReviewMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.Review;
import org.example.backend.model.User;
import org.example.backend.repository.ActivityRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.JwtUtil;
import org.example.backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository, JwtUtil jwtUtil, ActivityRepository activityRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewDTO> addReview(@RequestBody Review review, @RequestHeader("Authorization") String token) {
        // 1. Ištraukite vartotojo el. paštą iš token'o
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Jei autentifikacija nepavyksta, grąžiname klaidą
        }

        String userEmail = authentication.getName();  // Paima el. paštą iš autentifikacijos

        // 2. Gauti vartotoją pagal email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Gauti veiklą pagal ID
        Activity activity = activityRepository.findById(review.getActivity().getId())  // Assuming review has an Activity object
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // 4. Priskiriame vartotoją ir veiklą prie apžvalgos
        review.setUser(user);
        review.setActivity(activity);

        // 5. Išsaugome apžvalgą
        Review savedReview = reviewService.saveReview(review);

        // 6. Konvertuojame į DTO ir grąžiname su user ir activity
        ReviewDTO reviewDTO = ReviewMapper.toDTO(savedReview);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
    }

    @GetMapping("/activity/{id}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByActivity(@PathVariable Long id) {
        List<Review> reviews = reviewService.getReviewsForActivity(id);

        // Konvertuojame kiekvieną Review į ReviewDTO
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Review savedReview = reviewService.updateReview(id, updatedReview);

        ReviewDTO reviewDTO = ReviewMapper.toDTO(savedReview);

        return ResponseEntity.ok(reviewDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}