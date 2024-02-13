package com.cofisweak.cloudstorage.validator.objectPath;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectPathValidator implements ConstraintValidator<ObjectPath, String> {
    @Override
    public boolean isValid(String objectPath, ConstraintValidatorContext constraintValidatorContext) {
        if(objectPath == null) return false;
        Pattern pattern = Pattern.compile("^/([^\\\\!(){}\\[\\]+*`|~^%#&]*)*$");
        Matcher matcher = pattern.matcher(objectPath);
        return matcher.matches();
    }
}
