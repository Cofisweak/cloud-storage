package com.cofisweak.cloudstorage.repository.minio;

import com.cofisweak.cloudstorage.domain.UploadFile;
import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.mapper.MinioEntityMapper;
import com.cofisweak.cloudstorage.repository.StorageRepository;
import com.cofisweak.cloudstorage.web.dto.StorageEntityDto;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MinioRepository implements StorageRepository {

    private final MinioClient minioClient;
    private final MinioEntityMapper minioEntityMapper;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public void createFolder(String path) {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .object(path)
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to create new folder", e);
        }
    }

    @Override
    public void removeFolder(String path) {
        List<DeleteObject> deleteObjects =
                getFolderContent(path, true, true).stream()
                .map(StorageEntityDto::getStoragePath)
                .map(DeleteObject::new)
                .toList();
        removeObjects(deleteObjects);
    }

    private void removeObjects(List<DeleteObject> objects) {
        try {
            RemoveObjectsArgs args = RemoveObjectsArgs.builder()
                    .bucket(bucket)
                    .objects(objects)
                    .build();
            for (Result<DeleteError> removeObject : minioClient.removeObjects(args)) {
                removeObject.get();
            }
        } catch (Exception e) {
            throw new FileStorageException("Unable to remove objects", e);
        }
    }

    @Override
    public void removeFile(String path) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to remove object", e);
        }
    }

    @Override
    public boolean isObjectExist(String path) {
        try {
            StatObjectArgs args = StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build();
            minioClient.statObject(args);
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new FileStorageException("Unable to check object", e);
        }
    }

    @Override
    public List<StorageEntityDto> getFolderContent(String path) {
        return getFolderContent(path, false, false);
    }

    @Override
    public List<StorageEntityDto> getFolderContentRecursive(String path) {
        return getFolderContent(path, true, false);
    }

    private List<StorageEntityDto> getFolderContent(String path, boolean recursive, boolean includeSelf) {
        try {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .recursive(recursive)
                    .build();
            Iterable<Result<Item>> result = minioClient.listObjects(args);
            return minioEntityMapper.map(result, path, includeSelf);
        } catch (Exception e) {
            throw new FileStorageException("Unable to get folder content", e);
        }
    }

    @Override
    public InputStream downloadFile(String path) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build();
            return minioClient.getObject(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to download file", e);
        }
    }

    @Override
    public void uploadFiles(List<UploadFile> uploadFiles) {
        try {
            UploadSnowballObjectsArgs args = UploadSnowballObjectsArgs.builder()
                    .bucket(bucket)
                    .objects(getSnowballObjects(uploadFiles))
                    .build();
            minioClient.uploadSnowballObjects(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to upload files", e);
        }
    }

    @Override
    public StorageEntityDto getFile(String path) {
        try {
            ListObjectsArgs args = ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .recursive(false)
                    .maxKeys(1)
                    .build();
            Iterable<Result<Item>> result = minioClient.listObjects(args);
            return minioEntityMapper.map(result.iterator().next().get());
        } catch (Exception e) {
            throw new FileStorageException("Unable to get file", e);
        }
    }

    private List<SnowballObject> getSnowballObjects(List<UploadFile> uploadFiles) throws IOException {
        List<SnowballObject> objects = new ArrayList<>();
        for (UploadFile file : uploadFiles) {
            SnowballObject snowballObject = new SnowballObject(
                    file.getPath(),
                    file.getData(),
                    file.getData().available(),
                    null);
            objects.add(snowballObject);
        }
        return objects;
    }
}
