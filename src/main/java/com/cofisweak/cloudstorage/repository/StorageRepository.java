package com.cofisweak.cloudstorage.repository;

import com.cofisweak.cloudstorage.domain.UploadFile;
import com.cofisweak.cloudstorage.web.dto.StorageEntityDto;

import java.io.InputStream;
import java.util.List;

public interface StorageRepository {
    void createFolder(String path);
    void removeFolder(String path);
    void removeObjects(List<StorageEntityDto> objects);
    void removeFile(String path);
    boolean isObjectExist(String path);
    List<StorageEntityDto> getFolderContent(String path);
    List<StorageEntityDto> getFolderContentRecursive(String path);
    InputStream downloadFile(String path);
    void uploadFiles(List<UploadFile> uploadFiles);
    StorageEntityDto getFile(String storagePath);
}
