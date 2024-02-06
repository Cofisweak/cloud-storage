package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioStorageImpl implements FileStorageService {
    @Override
    public void createFolder(String rootPath, String folderName) {

    }

    @Override
    public void rename(String path) {

    }

    @Override
    public void copy(String fromPath, String toPath) {

    }

    @Override
    public void delete(String path) {

    }

    @Override
    public void save(MultipartFile file) {

    }
}
