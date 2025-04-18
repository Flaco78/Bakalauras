package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Invalid phone number format")
    private String phone;

    @Pattern(regexp = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-zA-Z0-9]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$",
            message = "Invalid website URL")
    private String website;

    @NotBlank(message = "Description cannot be empty")
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "provider_roles",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @NotEmpty(message = "At least one role is required")
    private Set<Role> roles;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Activity> activities;
}
