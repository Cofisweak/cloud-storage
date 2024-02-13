package com.cofisweak.cloudstorage.utils;

import lombok.experimental.UtilityClass;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@UtilityClass
public class Utils {

    public static String mapValidationResultToErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("<br>"));
    }
}
