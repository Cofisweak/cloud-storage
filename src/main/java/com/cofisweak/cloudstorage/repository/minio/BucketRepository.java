package com.cofisweak.cloudstorage.repository.minio;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
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
            log.error("An error occurred while creating bucket", e);
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
            log.error("An error occurred while checking bucket existing", e);
            throw new FileStorageException("Unable to check is bucket exist", e);
        }
    }
}
