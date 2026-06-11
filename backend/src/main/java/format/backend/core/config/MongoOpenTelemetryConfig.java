package format.backend.core.config;

import com.mongodb.observability.ObservabilitySettings;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class MongoOpenTelemetryConfig {

    @Bean
    MongoClientSettingsBuilderCustomizer mongoDbObservabilitySettings(ObservationRegistry observationRegistry) {
        return clientSettingsBuilder ->
                clientSettingsBuilder.observabilitySettings(ObservabilitySettings.micrometerBuilder()
                        .observationRegistry(observationRegistry)
                        .build());
    }
}
