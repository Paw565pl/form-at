package format.backend.core.config;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SlugifyConfig {

    @Bean
    Slugify slugify() {
        return Slugify.builder().build();
    }
}
