package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.ObjectName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchDto {

    @NotBlank(message = "Incorrect path")
    private String path;

    @NotBlank(message = "Query must be specified")
    @ObjectName(message = "Query contains illegal characters")
    private String query;
}