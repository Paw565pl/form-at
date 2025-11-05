package format.backend.form.config;

import format.backend.form.entity.FormEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FormEntityConfig implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        val indexOps = mongoTemplate.indexOps(FormEntity.class);

        val textIndexName = "forms_text_idx";
        val textIndexDefinition = TextIndexDefinition.builder()
                .named(textIndexName)
                .onField("name", 5.0F)
                .onField("description", 1.0F)
                .withDefaultLanguage("none")
                .withLanguageOverride("language")
                .build();

        try {
            indexOps.createIndex(textIndexDefinition);
        } catch (DataIntegrityViolationException e) {
            log.warn(
                    "Text index '{}' was found with a different definition. Dropping and recreating.",
                    textIndexName,
                    e);

            indexOps.dropIndex(textIndexName);
            indexOps.createIndex(textIndexDefinition);
        }
    }
}
