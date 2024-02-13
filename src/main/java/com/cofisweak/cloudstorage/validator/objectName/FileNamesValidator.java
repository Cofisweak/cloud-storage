package com.cofisweak.cloudstorage.validator.objectName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNamesValidator implements ConstraintValidator<FileNames, List<MultipartFile>> {
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        for (MultipartFile file : files) {
            Pattern pattern = Pattern.compile("^[^\\\\!(){}\\[\\]+*`|~^%#&]*$");
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return false;
            }
            Matcher matcher = pattern.matcher(originalFilename);
            if(!matcher.matches()) {
                return false;
            }
        }

        return true;
    }
}
