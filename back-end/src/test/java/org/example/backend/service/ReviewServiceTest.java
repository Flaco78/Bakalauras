package org.example.backend.service;

import org.example.backend.model.Review;
import org.example.backend.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewService = new ReviewService(reviewRepository);
    }

    @Test
    void saveReview_shouldSaveAndReturnReview() {
        Review review = new Review();
        when(reviewRepository.save(review)).thenReturn(review);

        Review result = reviewService.saveReview(review);

        assertEquals(review, result);
        verify(reviewRepository).save(review);
    }

    @Test
    void getReviewsForActivity_shouldReturnList() {
        Review r1 = new Review();
        Review r2 = new Review();
        List<Review> reviews = Arrays.asList(r1, r2);

        when(reviewRepository.findByActivityId(10L)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsForActivity(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));
        verify(reviewRepository).findByActivityId(10L);
    }

    @Test
    void updateReview_shouldUpdateAndReturnReview() {
        Review existing = new Review();
        existing.setRating(3);
        existing.setComment("Old comment");

        Review updated = new Review();
        updated.setRating(5);
        updated.setComment("New comment");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(existing)).thenReturn(existing);

        Review result = reviewService.updateReview(1L, updated);

        assertEquals(5, result.getRating());
        assertEquals("New comment", result.getComment());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(existing);
    }

    @Test
    void updateReview_shouldThrowIfNotFound() {
        Review updated = new Review();
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.updateReview(1L, updated));
        assertEquals("Review not found", ex.getMessage());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_shouldCallRepository() {
        reviewService.deleteReview(7L);
        verify(reviewRepository).deleteById(7L);
    }
}
