package org.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.backend.model.Provider;
import org.example.backend.model.ProviderRequest;
import org.example.backend.enums.ProviderType;

public class CompanyFieldsValidator implements ConstraintValidator<CompanyFieldsRequired, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        String companyName = null;
        String companyCode = null;
        ProviderType providerType = null;

        if (object instanceof ProviderRequest request) {
            companyName = request.getCompanyName();
            companyCode = request.getCompanyCode();
            providerType = request.getProviderType();
        } else if (object instanceof Provider provider) {
            companyName = provider.getCompanyName();
            companyCode = provider.getCompanyCode();
            providerType = provider.getProviderType();
        }

        if (providerType != ProviderType.COMPANY) {
            return true;
        }

        boolean valid = true;

        if (companyName == null || companyName.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Įmonės pavadinimas yra privalomas")
                    .addPropertyNode("companyName")
                    .addConstraintViolation();
            valid = false;
        }

        if (companyCode == null || companyCode.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Mokesčių mokėtojo kodas yra privalomas")
                    .addPropertyNode("companyCode")
                    .addConstraintViolation();
            valid = false;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
        }

        return valid;
    }
}