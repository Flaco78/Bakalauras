package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.enums.DeliveryMethod;
import org.example.backend.enums.Gender;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Child's name is required!")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Birth date is required!")
    @Past(message = "Birth date must be in the past!")
    private LocalDate birthDate;
    @AssertTrue(message = "Child must be between 6 and 10 years old!")
    private boolean isValidAge() {
        if (birthDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthDate.getYear();
        return age >= 6 && age <= 11;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private int maxActivityDuration;  // Laiko limitas minutėmis

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryMethod preferredDeliveryMethod;  // ONLINE arba ONSITE

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonBackReference
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonBackReference
    private Set<ActivityInteraction> activityInteractions = new HashSet<>();

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "child_interests", joinColumns = @JoinColumn(name = "child_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<ActivityCategory> interests = new HashSet<>();
}