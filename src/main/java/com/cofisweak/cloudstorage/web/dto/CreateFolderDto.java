package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.objectName.ObjectName;
import com.cofisweak.cloudstorage.validator.objectPath.ObjectPath;
import lombok.Data;

@Data
public class CreateFolderDto {

    @ObjectPath(message = "Incorrect path")
    private String path;
    @ObjectName(message = "Folder name contains illegal characters")
    private String folderName;
}