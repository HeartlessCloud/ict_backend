package com.laojiahuo.ictproject.config.MinIO;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioProperties {

    /**
     * 连接地址
     */
    private String endpoint;
    /**
     * 用户名
     */
    private String accessKey;
    /**
     * 密码
     */
    private String secretKey;

    /**
     * bucket 名称
     */
    private String bucketName;

    /**
     * 域名
     */
    private String nginxHost;


}