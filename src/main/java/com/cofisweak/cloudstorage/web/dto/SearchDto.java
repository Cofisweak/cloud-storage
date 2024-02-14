package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.objectName.ObjectName;
import com.cofisweak.cloudstorage.validator.objectPath.ObjectPath;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchDto {

    @NotBlank(message = "Incorrect path")
    @ObjectPath(message = "Incorrect path")
    private String path;

    @NotBlank(message = "Incorrect object name")
    @ObjectName(message = "Folder object name contains illegal characters")
    private String query;
}