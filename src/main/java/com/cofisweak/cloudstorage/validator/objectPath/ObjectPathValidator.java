package com.cofisweak.cloudstorage.validator.objectPath;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectPathValidator implements ConstraintValidator<ObjectPath, String> {
    @Override
    public boolean isValid(String objectName, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile("^/([^\\\\!(){}\\[\\]+*`|~^%#&]*)*$");
        Matcher matcher = pattern.matcher(objectName);
        return matcher.matches();
    }
}
