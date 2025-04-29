package org.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.backend.model.Provider;
import org.example.backend.model.ProviderRequest;
import org.example.backend.enums.ProviderType;

public class IndividualNameValidator implements ConstraintValidator<IndividualNameRequired, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        String name = null;
        ProviderType providerType = null;

        if (object instanceof Provider provider) {
            name = provider.getName();
            providerType = provider.getProviderType();
        } else if (object instanceof ProviderRequest request) {
            name = request.getName();
            providerType = request.getProviderType();
        }

        if (providerType != ProviderType.INDIVIDUAL) {
            return true;
        }

        if (name == null || name.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Vardas yra privalomas individualiems teikėjams")
                    .addPropertyNode("name")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}