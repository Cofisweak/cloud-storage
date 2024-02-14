package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.domain.UploadFile;
import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import com.cofisweak.cloudstorage.repository.StorageRepository;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.cofisweak.cloudstorage.utils.PathUtils.*;

@Service
@RequiredArgsConstructor
public class MinioStorageImpl implements FileStorageService {

    private final StorageRepository storageRepository;

    @Override
    public void createFolder(CreateFolderDto dto) {
        String storagePath = resolveToStoragePath(dto.getPath() + dto.getFolderName()) + "/";
        if (storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("Folder with this name already exists");
        }
        storageRepository.createFolder(storagePath);
    }

    @Override
    public List<StorageEntityDto> getFolderContent(String path) {
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("Folder not found");
        }
        return storageRepository.getFolderContent(storagePath).stream()
                .sorted(Comparator
                        .comparing(StorageEntityDto::isDirectory).reversed()
                        .thenComparing(StorageEntityDto::getObjectName))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFolder(DeleteDto dto) {
        String storagePath = resolveToStoragePath(dto.getPath() + dto.getObjectName()) + "/";
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("Folder not found");
        }
        storageRepository.removeFolder(storagePath);
    }

    @Override
    @SneakyThrows({IOException.class})
    public void upload(UploadDto dto) {
        Set<String> foldersToCheck = new HashSet<>();
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : dto.getFiles()) {
            String localPath = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new FileStorageException("Unable to upload file. File name isn't valid"));

            String localParentFolder = getRootFolder(localPath);
            String filename = extractObjectName(localPath);

            foldersToCheck.addAll(getAllSubfolderPaths(dto.getPath(), localParentFolder));

            String composedPath = composePath(dto.getPath(), localParentFolder, filename);
            String storagePath = resolveToStoragePath(composedPath);

            UploadFile uploadFile = new UploadFile(storagePath, file.getInputStream());
            uploadFiles.add(uploadFile);
        }
        createFoldersIfNotExist(foldersToCheck);
        storageRepository.uploadFiles(uploadFiles);
    }

    private Set<String> getAllSubfolderPaths(String path, String localParentFolder) {
        Set<String> result = new HashSet<>();
        while (!localParentFolder.equals("/")) {
            String composedPath = composePath(path, localParentFolder);
            String storagePath = resolveToStoragePath(composedPath);
            result.add(storagePath);
            localParentFolder = getRootFolder(localParentFolder);
        }
        return result;
    }

    private void createFoldersIfNotExist(Set<String> folders) {
        for (String folderPath : folders) {
            if (!storageRepository.isObjectExist(folderPath)) {
                storageRepository.createFolder(folderPath);
            }
        }
    }

    @Override
    public void deleteFile(DeleteDto dto) {
        String storagePath = resolveToStoragePath(dto.getPath() + dto.getObjectName());
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("File not found");
        }
        storageRepository.removeFile(storagePath);
    }

    @Override
    public DownloadDto downloadFile(String path) {
        String storagePath = resolveToStoragePath(path);
        InputStream stream = storageRepository.downloadFile(storagePath);
        String filename = extractObjectName(path);
        return new DownloadDto(filename, stream);
    }

    @Override
    public List<StorageEntityDto> search(SearchDto dto) {
        String storagePath = resolveToStoragePath(dto.getPath());
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("Folder not found");
        }
        return storageRepository.getFolderContentRecursive(storagePath).stream()
                .filter(object -> isObjectNameContainsQuery(object.getObjectName(), dto.getQuery()))
                .sorted(Comparator
                        .comparing(StorageEntityDto::isDirectory).reversed()
                        .thenComparing(StorageEntityDto::getObjectName))
                .collect(Collectors.toList());
    }

    private boolean isObjectNameContainsQuery(String objectName, String query) {
        return objectName.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public StorageEntityDto getFile(String path) {
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("File not found");
        }
        return storageRepository.getFile(storagePath);
    }

    @Override
    public void createUserDirectory(Long userId) {
        String folderName = getUserRootFolderName(userId);
        String storagePath = folderName + "/";
        storageRepository.createFolder(storagePath);
    }
}
