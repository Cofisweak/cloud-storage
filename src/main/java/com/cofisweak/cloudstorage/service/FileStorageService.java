package com.cofisweak.cloudstorage.service;

import com.cofisweak.cloudstorage.web.dto.*;

import java.io.OutputStream;
import java.util.List;

public interface FileStorageService {
    void createFolder(CreateFolderDto dto);
    List<StorageEntityDto> getFolderContent(String path);
    void deleteFolder(DeleteDto dto);
    void upload(UploadDto dto);
    void createUserDirectory(Long userId);
    void deleteFile(DeleteDto dto);
    DownloadDto downloadFile(String path);
    void downloadFolder(String path, OutputStream responseStream);
    List<StorageEntityDto> search(SearchDto dto);
    StorageEntityDto getFile(String path);
}
