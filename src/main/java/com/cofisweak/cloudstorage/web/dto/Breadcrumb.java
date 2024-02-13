package com.cofisweak.cloudstorage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Breadcrumb {
    private String objectName;
    private String path;
}
