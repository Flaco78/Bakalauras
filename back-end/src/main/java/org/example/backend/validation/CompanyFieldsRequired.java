package org.example.backend.validation;

import com.auth0.jwt.interfaces.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CompanyFieldsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CompanyFieldsRequired {
    String message() default "Įmonės pavadinimas ir mokesčių mokėtojo kodas yra privalomi, jei pasirinkta 'Įmonė'";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
