package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.domain.UploadFile;
import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import com.cofisweak.cloudstorage.repository.StorageRepository;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public void upload(UploadDto dto) {
        Set<String> foldersToCheck = new HashSet<>();
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : dto.getFiles()) {
            String localPath = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new FileStorageException("Unable to upload file. File name isn't valid"));

            String localParentFolder = PathUtils.getRootFolder(localPath);
            String filename = PathUtils.extractObjectName(localPath);

            foldersToCheck.addAll(getAllSubfolderPaths(dto.getPath(), localParentFolder));

            String composedPath = composePath(dto.getPath(), localParentFolder, filename);
            String storagePath = resolveToStoragePath(composedPath);

            try {
                UploadFile uploadFile = new UploadFile(storagePath, file.getInputStream());
                uploadFiles.add(uploadFile);
            } catch (IOException e) {
                log.error("An error occurred while processing download file request", e);
            }
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
            localParentFolder = PathUtils.getRootFolder(localParentFolder);
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
        String filename = PathUtils.extractObjectName(path);
        return new DownloadDto(filename, stream);
    }

    @Override
    public void downloadFolder(String path, OutputStream responseStream) {
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new FileStorageException("Folder not found");
        }
        List<StorageEntityDto> objects = storageRepository.getFolderContentRecursive(storagePath).stream()
                .filter(storageEntityDto -> !storageEntityDto.isDirectory())
                .toList();
        if (objects.isEmpty()) {
            throw new FileStorageException("Folder is empty");
        }
        try (ZipOutputStream zip = new ZipOutputStream(responseStream)) {
            for (StorageEntityDto object : objects) {
                writeObjectToZip(object, zip, path);
            }
        } catch (ClientAbortException ignored) {
        } catch (IOException e) {
            log.error("An error occurred while processing download folder request", e);
        }
    }

    private void writeObjectToZip(StorageEntityDto object, ZipOutputStream zip, String rootPath) throws IOException {
        String relativePath = object.getPath().substring(rootPath.length());
        ZipEntry zipEntry = new ZipEntry(relativePath);
        zip.putNextEntry(zipEntry);
        String fileStoragePath = resolveToStoragePath(object.getPath());
        try (InputStream fileStream = storageRepository.downloadFile(fileStoragePath)) {
            byte[] buffer = fileStream.readAllBytes();
            zip.write(buffer);
        }
        zip.closeEntry();
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
            throw new ObjectNotFoundException("File not found");
        }
        return storageRepository.getFile(storagePath);
    }

    @Override
    public void renameFile(RenameDto dto) {
        String path = composePath(dto.getPath(), dto.getOldObjectName());
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("File not found");
        }
        String newPath = composePath(dto.getPath(), dto.getNewObjectName());
        String newStoragePath = resolveToStoragePath(newPath);
        if (storageRepository.isObjectExist(newStoragePath)) {
            throw new FileStorageException("File with this name already exist");
        }
        storageRepository.copyObject(storagePath, newStoragePath);
        storageRepository.removeFile(storagePath);
    }

    @Override
    public void renameFolder(RenameDto dto) {
        String path = composePath(dto.getPath(), dto.getOldObjectName()) + "/";
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("File not found");
        }

        String newPath = composePath(dto.getPath(), dto.getNewObjectName()) + "/";
        String newStoragePath = resolveToStoragePath(newPath);
        if (storageRepository.isObjectExist(newStoragePath)) {
            throw new FileStorageException("File with this name already exist");
        }

        copyFolder(storagePath, newStoragePath, path, newPath);
    }

    private void copyFolder(String storagePath, String newStoragePath, String path, String newPath) {
        storageRepository.copyObject(storagePath, newStoragePath);
        for (StorageEntityDto object : storageRepository.getFolderContentRecursive(storagePath)) {
            String oldObjectPath = object.getPath();
            String oldObjectStoragePath = resolveToStoragePath(oldObjectPath);

            String newObjectPath = newPath + oldObjectPath.substring(path.length());
            String newObjectStoragePath = resolveToStoragePath(newObjectPath);

            storageRepository.copyObject(oldObjectStoragePath, newObjectStoragePath);
        }
        storageRepository.removeFolder(storagePath);
    }

    @Override
    public void createUserDirectory(Long userId) {
        String folderName = getUserRootFolderName(userId);
        String storagePath = folderName + "/";
        storageRepository.createFolder(storagePath);
    }

    private String composePath(String... paths) {
        String resultPath = Arrays.stream(paths)
                .map(path -> path.split("/"))
                .flatMap(Arrays::stream)
                .filter(path -> !path.isBlank())
                .collect(Collectors.joining("/", "/", ""));

        if (paths[paths.length - 1].endsWith("/")) {
            resultPath += "/";
        }

        return resultPath;
    }

    private String resolveToStoragePath(String path) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        String userRootFolderName = getUserRootFolderName(userId);
        return userRootFolderName + path;
    }

    private String getUserRootFolderName(Long id) {
        String pattern = "user-%d-files";
        return pattern.formatted(id);
    }
}
