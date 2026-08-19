package format.backend.upload.config;

import format.backend.upload.properties.S3Properties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
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

    @Bean
    ApplicationRunner initBucket(S3Properties s3Properties, MinioClient minioClient) {
        return _ -> {
            val bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(s3Properties.bucket()).build());
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(s3Properties.bucket()).build());
                log.info("Bucket '{}' created.", s3Properties.bucket());
            }
        };
    }
}
