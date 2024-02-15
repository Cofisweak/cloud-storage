package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.UploadFilenames;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadDto {

    @NotEmpty(message = "Files must be specified")
    @UploadFilenames(message = "One of folder or file contain illegal symbols")
    private List<MultipartFile> files;
}