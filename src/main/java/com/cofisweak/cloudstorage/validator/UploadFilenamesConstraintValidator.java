package com.cofisweak.cloudstorage.validator;

import com.cofisweak.cloudstorage.utils.PathUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
public class UploadFilenamesConstraintValidator implements ConstraintValidator<UploadFilenames, List<MultipartFile>> {

    private final ObjectNameValidator validator;
    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return false;
            }
            String filename = PathUtils.extractObjectName(originalFilename);
            if (!validator.isValid(filename)) {
                return false;
            }
        }

        return true;
    }
}
