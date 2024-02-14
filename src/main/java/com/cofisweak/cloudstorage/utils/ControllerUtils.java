package com.cofisweak.cloudstorage.utils;

import com.cofisweak.cloudstorage.web.dto.Breadcrumb;
import lombok.experimental.UtilityClass;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ControllerUtils {

    public static String mapValidationResultToErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("<br>"));
    }

    public static List<Breadcrumb> createBreadcrumbsFromPath(String path) {
        List<Breadcrumb> result = new ArrayList<>();

        result.add(new Breadcrumb("Home", "/"));

        String[] objects = path.split("/");
        String currentPath = "/";
        for (String object : objects) {
            if(object.isBlank()) continue;
            currentPath += object + "/";
            result.add(new Breadcrumb(object, currentPath));
        }

        if(!path.endsWith("/") && result.size() > 1) {
            result.remove(result.size() - 1);
        }
        return result;
    }
}
