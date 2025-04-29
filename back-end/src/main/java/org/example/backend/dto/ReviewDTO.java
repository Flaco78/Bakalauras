package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String comment;
    private int rating;
    private UserDTO user;
    private ActivityDTO activity;

    public ReviewDTO(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }
}