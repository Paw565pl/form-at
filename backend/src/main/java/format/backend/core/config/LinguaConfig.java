package format.backend.core.config;

import static com.github.pemistahl.lingua.api.Language.ENGLISH;
import static com.github.pemistahl.lingua.api.Language.POLISH;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinguaConfig {

    @Bean
    public LanguageDetector languageDetector() {
        return LanguageDetectorBuilder.fromLanguages(ENGLISH, POLISH)
                .withPreloadedLanguageModels()
                .withLowAccuracyMode()
                .build();
    }
}
