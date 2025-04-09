package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Review;
import org.example.backend.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForActivity(Long activityId) {
        return reviewRepository.findByActivityId(activityId);
    }

    public Review updateReview(Long id, Review updatedReview) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(updatedReview.getRating());
            review.setComment(updatedReview.getComment());
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Review not found");
        }
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
