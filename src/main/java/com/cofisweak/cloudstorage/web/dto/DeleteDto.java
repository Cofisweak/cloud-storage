package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.ObjectName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteDto {

    @NotBlank(message = "Incorrect object name")
    @ObjectName(message = "Object name contains illegal characters")
    private String objectName;
}
