package com.cofisweak.cloudstorage.validator.objectName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FileNamesValidator.class)
public @interface FileNames {

    String message() default "Invalid filenames";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
