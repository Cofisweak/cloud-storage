package com.cofisweak.cloudstorage.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectNameConstraintValidator implements ConstraintValidator<ObjectName, String> {

    private final ObjectNameValidator validator;
    @Override
    public boolean isValid(String objectName, ConstraintValidatorContext constraintValidatorContext) {
        return validator.isValid(objectName);
    }
}
