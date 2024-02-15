package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.ObjectName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFolderDto {

    @NotBlank(message = "Incorrect folder name")
    @ObjectName(message = "Folder name contains illegal characters")
    private String folderName;
}