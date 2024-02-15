package com.cofisweak.cloudstorage.validator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObjectNameValidator {

    public boolean isValid(String objectName) {
        if(objectName == null) return false;
        var illegalCharacters = List.of('/', '\\', '#', '&');
        for (Character illegalCharacter : illegalCharacters) {
            if (objectName.contains(illegalCharacter.toString())) {
                return false;
            }
        }
        return true;
    }
}
