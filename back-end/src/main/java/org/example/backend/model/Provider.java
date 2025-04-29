package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.enums.ProviderType;
import org.example.backend.validation.CompanyFieldsRequired;
import org.example.backend.validation.IndividualNameRequired;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@CompanyFieldsRequired
@IndividualNameRequired
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotBlank(message = "El. pašto laukelis yra reikalingas")
    @Email(message = "Neteisingas el. pašto formatas")
    private String email;

    @NotBlank(message = "Slaptažodis yra reikalingas")
    @Size(min = 6, message = "Slaptažodis turi būti bent šešių skaitmenų")
    private String password;

    @NotNull(message = "Telefonas yra reikalingas")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Neteisingas telefono numerio formatas")
    private String phone;

    @Pattern(regexp = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-zA-Z0-9]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$",
            message = "Neteisingas tinklapio adresas")
    private String website;

    @NotBlank(message = "Aprašymas negali būti tuščias")
    @Column(nullable = false)
    private String description;

    private String companyName;
    private String companyCode;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "provider_roles",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Activity> activities;
}
