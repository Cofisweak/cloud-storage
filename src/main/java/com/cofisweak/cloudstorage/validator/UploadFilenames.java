package com.cofisweak.cloudstorage.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UploadFilenamesConstraintValidator.class)
public @interface UploadFilenames {

    String message() default "Invalid filenames";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
