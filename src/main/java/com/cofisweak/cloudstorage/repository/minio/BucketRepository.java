package com.cofisweak.cloudstorage.repository.minio;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BucketRepository {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void createRootBucket() {
        try {
            if(isRootBucketExists()) return;

            MakeBucketArgs args = MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build();
            minioClient.makeBucket(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to create bucket", e);
        }
    }

    public boolean isRootBucketExists() {
        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            return minioClient.bucketExists(args);
        } catch (Exception e) {
            throw new FileStorageException("Unable to check is bucket exist", e);
        }
    }
}
