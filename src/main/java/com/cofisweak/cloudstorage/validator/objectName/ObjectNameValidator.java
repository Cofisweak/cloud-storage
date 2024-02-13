package com.cofisweak.cloudstorage.validator.objectName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectNameValidator implements ConstraintValidator<ObjectName, String> {
    @Override
    public boolean isValid(String objectName, ConstraintValidatorContext constraintValidatorContext) {
        if(objectName == null) return false;
        Pattern pattern = Pattern.compile("^[^/\\\\\"'!(){}\\[\\]+*`|~^%#&]+$");
        Matcher matcher = pattern.matcher(objectName);
        return matcher.matches();
    }
}
