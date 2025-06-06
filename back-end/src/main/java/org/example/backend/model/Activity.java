package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.ActivityStatus;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.enums.PriceType;

import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description cannot be empty")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Description cannot be empty")
    private String descriptionChild;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Category cannot be empty")
    private ActivityCategory category;

    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Delivery method cannot be null")
    private DeliveryMethod deliveryMethod;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Price type cannot be null")
    private PriceType priceType;

    @ManyToOne
    @JsonIgnoreProperties("activities")
    @JsonBackReference
    @JoinColumn(name = "provider_id", nullable = false)
    @NotNull(message = "Provider cannot be null")
    private Provider provider;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityTimeSlot> timeSlots = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status = ActivityStatus.PENDING;
}
