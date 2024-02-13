package com.cofisweak.cloudstorage.web.dto;

import com.cofisweak.cloudstorage.validator.objectName.FileNames;
import com.cofisweak.cloudstorage.validator.objectPath.ObjectPath;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadDto {

    @ObjectPath(message = "Incorrect path")
    private String path;

    @FileNames(message = "One of folder or file contain illegal symbols")
    private List<MultipartFile> files;
}