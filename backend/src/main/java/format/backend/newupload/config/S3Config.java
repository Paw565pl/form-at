package format.backend.newupload.config;

import format.backend.newupload.properties.S3Properties;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class S3Config {

    @Bean
    MinioClient minioClient(S3Properties s3Properties) {
        return MinioClient.builder()
                .endpoint(s3Properties.url())
                .credentials(s3Properties.accessKey(), s3Properties.secretKey())
                .region(s3Properties.region())
                .build();
    }
}
