package format.backend.storage.config;

import format.backend.storage.properties.MinioProperties;
import io.minio.MinioClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.time.Duration;
import lombok.val;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MinioConfig {

    @Bean
    MinioClient minioClient(MinioProperties minioProperties) {
        val publicUri = URI.create(minioProperties.getEndpoint());
        val proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(publicUri.getHost(), publicUri.getPort()));
        val httpClient = new OkHttpClient.Builder()
                .proxy(proxy)
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build();

        return MinioClient.builder()
                .endpoint(minioProperties.getPublicEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .httpClient(httpClient)
                .build();
    }
}
