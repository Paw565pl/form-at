package format.backend.upload.config;

import format.backend.upload.properties.S3Properties;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
class S3Config {

    @Bean
    S3Client s3Client(S3Properties s3Properties) {
        return S3Client.builder()
                .endpointOverride(URI.create(s3Properties.getUrl()))
                .region(s3Properties.getRegion())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .forcePathStyle(s3Properties.getForcePathStyle())
                .build();
    }
}
