package format.backend.upload.config;

import format.backend.upload.properties.S3Properties;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class S3Config {

    @Bean
    MinioClient minioClient(S3Properties s3Properties) {
        return MinioClient.builder()
                .endpoint(s3Properties.getUrl())
                .credentials(s3Properties.getAccessKey(), s3Properties.getSecretKey())
                .region(s3Properties.getRegion())
                .build();
    }
}
