package org.example.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IndividualNameValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IndividualNameRequired {
    String message() default "Vardas yra privalomas, jei pasirinkta 'Individualus'";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}