package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

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
    @AssertTrue(message = "Child must be between 6 and 18 years old!")
    private boolean isValidAge() {
        if (birthDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthDate.getYear();
        return age >= 6 && age <= 10;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonBackReference
    private User parent;
}