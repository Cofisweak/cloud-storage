package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.ObjectName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenameDto {

    @NotBlank(message = "Incorrect old object name")
    @ObjectName(message = "Old object name contains illegal characters")
    private String oldObjectName;

    @NotBlank(message = "Incorrect new object name")
    @ObjectName(message = "New object name contains illegal characters")
    private String newObjectName;
}