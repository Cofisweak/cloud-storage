package com.cofisweak.cloudstorage.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {
    private String url;
    private String bucket;
    private String accessKey;
    private String secretKey;
}
