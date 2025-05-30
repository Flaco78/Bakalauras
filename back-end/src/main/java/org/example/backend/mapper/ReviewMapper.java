package org.example.backend.mapper;

import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.ReviewDTO;
import org.example.backend.dto.UserDTO;
import org.example.backend.model.Review;

import java.util.stream.Collectors;

public class ReviewMapper {

    private ReviewMapper() {

    }

    public static ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getComment(),
                review.getRating(),
                new UserDTO(
                        review.getUser().getId(),
                        review.getUser().getEmail(),
                        review.getUser().getAddress(),
                      null
                ),
                new ActivityDTO(
                        review.getActivity().getId(),
                        review.getActivity().getTitle(),
                        review.getActivity().getDescription(),
                        review.getActivity().getDescriptionChild(),
                        review.getActivity().getCategory(),
                        review.getActivity().getImageUrl(),
                        review.getActivity().getLocation(),
                        review.getActivity().getDeliveryMethod(),
                        review.getActivity().getDurationMinutes(),
                        review.getActivity().getPrice(),
                        review.getActivity().getPriceType(),
                        review.getActivity().getProvider().getName(),
                        review.getActivity().getProvider().getEmail(),
                        review.getActivity().getProvider().getCompanyName(),
                        review.getActivity().getProvider().getPhone(),
                        review.getActivity().getProvider().getDescription(),
                        review.getActivity().getProvider().getProviderType(),
                        review.getActivity().getProvider().getId(),
                        null,
                        null
                )
        );
    }
}