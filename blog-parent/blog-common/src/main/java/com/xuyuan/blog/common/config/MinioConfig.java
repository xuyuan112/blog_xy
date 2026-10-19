package com.xuyuan.blog.common.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置
 *
 * <p>对象存储配置, 读取 application.yml 中的 minio 前缀配置。</p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true")
public class MinioConfig {

    /** 是否启用对象存储客户端 */
    private boolean enabled;

    /** 服务地址 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 私密密钥 */
    private String secretKey;

    /** 存储桶 */
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
