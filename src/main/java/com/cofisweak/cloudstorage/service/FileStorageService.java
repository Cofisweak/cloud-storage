package com.cofisweak.cloudstorage.service;

import com.cofisweak.cloudstorage.web.dto.DownloadFileDto;
import com.cofisweak.cloudstorage.web.dto.StorageEntityDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

public interface FileStorageService {
    void createFolder(String path, String folderName);
    List<StorageEntityDto> getFolderContent(String path);
    void deleteFolder(String path, String folderName);
    void upload(String path, List<MultipartFile> files);
    void renameFolder(String path, String oldFolderName, String newFolderName);
    void createUserDirectory(Long userId);
    void deleteFile(String path, String filename);
    DownloadFileDto downloadFile(String path);
    void downloadFolder(String path, OutputStream responseStream);
    List<StorageEntityDto> search(String path, String query);
    StorageEntityDto getFile(String path);
    void renameFile(String path, String oldFilename, String newFilename);
}
