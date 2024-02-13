package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.objectName.FileNames;
import com.cofisweak.cloudstorage.validator.objectPath.ObjectPath;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadDto {

    @NotBlank(message = "Incorrect path")
    @ObjectPath(message = "Incorrect path")
    private String path;

    @NotNull(message = "Files must be specified")
    @FileNames(message = "One of folder or file contain illegal symbols")
    private List<MultipartFile> files;
}