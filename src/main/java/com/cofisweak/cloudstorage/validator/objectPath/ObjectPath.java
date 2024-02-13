package com.cofisweak.cloudstorage.validator.objectPath;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ObjectPathValidator.class)
public @interface ObjectPath {

    String message() default "Invalid object path";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
