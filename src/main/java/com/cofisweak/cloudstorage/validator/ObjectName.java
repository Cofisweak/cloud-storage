package com.cofisweak.cloudstorage.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ObjectNameConstraintValidator.class)
public @interface ObjectName {

    String message() default "Invalid object name";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
