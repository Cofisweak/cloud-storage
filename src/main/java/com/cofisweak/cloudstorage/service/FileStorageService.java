package com.cofisweak.cloudstorage.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    void createFolder(String rootPath, String folderName);
    void rename(String path);
    void copy(String fromPath, String toPath);
    void delete(String path);
    void save(MultipartFile file);
    // etc later
}
