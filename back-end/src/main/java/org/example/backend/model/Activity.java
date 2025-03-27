package org.example.backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Description cannot be empty")
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Description cannot be empty")
    private String descriptionChild;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Category cannot be empty")
    private ActivityCategory category;

    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "provider_id", nullable = false)
    @NotNull(message = "Provider cannot be null")
    private Provider provider;
}
