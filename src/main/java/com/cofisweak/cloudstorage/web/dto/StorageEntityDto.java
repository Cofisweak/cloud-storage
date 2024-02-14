package com.cofisweak.cloudstorage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageEntityDto {
    private String objectName;
    private String path;
    private String storagePath;
    private boolean isDirectory;
    private String size;
    private String createdAt;
}
