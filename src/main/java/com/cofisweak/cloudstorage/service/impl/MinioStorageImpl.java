package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.domain.UploadFile;
import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.domain.exception.ObjectAlreadyExistException;
import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import com.cofisweak.cloudstorage.domain.exception.UnableToUploadFilesException;
import com.cofisweak.cloudstorage.repository.StorageRepository;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.DownloadFileDto;
import com.cofisweak.cloudstorage.web.dto.StorageEntityDto;
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
    public void createFolder(String path, String folderName) {
        String storagePath = resolveToStoragePath(path + folderName) + "/";
        if (storageRepository.isObjectExist(storagePath)) {
            throw new ObjectAlreadyExistException("Folder with this name already exists");
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
    public void deleteFolder(String path, String folderName) {
        String storagePath = resolveToStoragePath(path + folderName) + "/";
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("Folder not found");
        }
        storageRepository.removeFolder(storagePath);
    }

    @Override
    public void upload(String path, List<MultipartFile> files) {
        Set<String> foldersToCheck = new HashSet<>();
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String localPath = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new UnableToUploadFilesException("Unable to upload files. One of files isn't valid"));

            String localParentFolder = PathUtils.getRootFolder(localPath);
            String filename = PathUtils.extractObjectName(localPath);

            foldersToCheck.addAll(getAllSubfolderPaths(path, localParentFolder));

            String composedPath = composePath(path, localParentFolder, filename);
            String storagePath = resolveToStoragePath(composedPath);

            try {
                UploadFile uploadFile = new UploadFile(storagePath, file.getInputStream());
                uploadFiles.add(uploadFile);
            } catch (IOException e) {
                log.error("An error occurred while processing download file request", e);
                throw new UnableToUploadFilesException("Unable to upload files");
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
    public void deleteFile(String path, String filename) {
        String storagePath = resolveToStoragePath(path + filename);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("File not found");
        }
        storageRepository.removeFile(storagePath);
    }

    @Override
    public DownloadFileDto downloadFile(String path) {
        String storagePath = resolveToStoragePath(path);
        InputStream stream = storageRepository.downloadFile(storagePath);
        String filename = PathUtils.extractObjectName(path);
        return new DownloadFileDto(filename, stream);
    }

    @Override
    public void downloadFolder(String path, OutputStream responseStream) {
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("Folder not found");
        }
        List<StorageEntityDto> objects = storageRepository.getFolderContentRecursive(storagePath).stream()
                .filter(storageEntityDto -> !storageEntityDto.isDirectory())
                .toList();
        try (ZipOutputStream zip = new ZipOutputStream(responseStream)) {
            for (StorageEntityDto object : objects) {
                writeObjectToZip(object, zip, path);
            }
        } catch (ClientAbortException ignored) {
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while processing download folder request", e);
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
    public List<StorageEntityDto> search(String path, String query) {
        String storagePath = resolveToStoragePath(path);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("Search path isn't valid");
        }
        return storageRepository.getFolderContentRecursive(storagePath).stream()
                .filter(object -> isObjectNameContainsQuery(object.getObjectName(), query))
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
    public void renameFile(String path, String oldFilename, String newFilename) {
        String composedPath = composePath(path, oldFilename);
        String storagePath = resolveToStoragePath(composedPath);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("File not found");
        }
        String newPath = composePath(path, newFilename);
        String newStoragePath = resolveToStoragePath(newPath);
        if (storageRepository.isObjectExist(newStoragePath)) {
            throw new ObjectAlreadyExistException("File with this name already exist");
        }
        storageRepository.copyObject(storagePath, newStoragePath);
        storageRepository.removeFile(storagePath);
    }

    @Override
    public void renameFolder(String path, String oldFolderName, String newFolderName) {
        String composedPath = composePath(path, oldFolderName) + "/";
        String storagePath = resolveToStoragePath(composedPath);
        if (!storageRepository.isObjectExist(storagePath)) {
            throw new ObjectNotFoundException("Folder not found");
        }

        String newPath = composePath(path, newFolderName) + "/";
        String newStoragePath = resolveToStoragePath(newPath);
        if (storageRepository.isObjectExist(newStoragePath)) {
            throw new ObjectAlreadyExistException("Folder with this name already exist");
        }

        copyFolder(storagePath, newStoragePath, composedPath, newPath);
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
