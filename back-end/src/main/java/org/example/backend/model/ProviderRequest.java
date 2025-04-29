package org.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.ProviderStatus;
import org.example.backend.enums.ProviderType;
import org.example.backend.validation.CompanyFieldsRequired;
import org.example.backend.validation.IndividualNameRequired;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CompanyFieldsRequired
@IndividualNameRequired
public class ProviderRequest {
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


    @NotBlank(message = "Aprašymas negali būti tuščias")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Telefonas yra reikalingas")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Neteisingas telefono numerio formatas")
    private String phone;


    @Pattern(regexp = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-zA-Z0-9]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)$",
            message = "Neteisingas tinklapio adresas")
    private String website;

    private String companyName;
    private String companyCode;


    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    private String rejectionReason;
}
