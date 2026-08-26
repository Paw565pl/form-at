package format.backend.form.config;

import format.backend.form.domain.entity.FormEntity;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FormEntityConfig implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;

    @Override
    @Retryable(includes = RuntimeException.class, maxRetries = 3, delay = 1000, multiplier = 2)
    public void run(@NonNull ApplicationArguments args) {
        if (!mongoTemplate.collectionExists(FormEntity.class)) mongoTemplate.createCollection(FormEntity.class);

        val textIndexName = "default";
        val textIndexDefinition = Document.parse("""
                {
                  "mappings": {
                    "dynamic": false,
                    "fields": {
                      "name": [
                        {
                          "type": "string",
                          "analyzer": "lucene.standard",
                          "multi": {
                            "en": { "type": "string", "analyzer": "lucene.english" },
                            "pl": { "type": "string", "analyzer": "lucene.morfologik" }
                          }
                        },
                        {
                          "type": "autocomplete",
                          "tokenization": "edgeGram",
                          "minGrams": 2,
                          "maxGrams": 15
                        }
                      ],
                      "description": {
                        "type": "string",
                        "analyzer": "lucene.standard",
                        "multi": {
                          "en": { "type": "string", "analyzer": "lucene.english" },
                          "pl": { "type": "string", "analyzer": "lucene.morfologik" }
                        }
                      },
                      "status": {
                        "type": "token"
                      },
                      "language": {
                        "type": "token"
                      },
                      "estimatedDurationSeconds": {
                        "type": "number"
                      },
                      "allowsGuestSubmissions": {
                        "type": "boolean"
                      },
                      "authorId": {
                        "type": "token"
                      }
                    }
                  }
                }""");
        val formCollection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(FormEntity.class));

        var doesTextIndexExist = false;
        for (val searchIndex : formCollection.listSearchIndexes()) {
            if (Objects.equals(searchIndex.getString("name"), textIndexName)) {
                doesTextIndexExist = true;
                break;
            }
        }

        if (doesTextIndexExist) {
            formCollection.updateSearchIndex(textIndexName, textIndexDefinition);
            log.info("Forms collection text search index updated successfully.");
        } else {
            formCollection.createSearchIndex(textIndexName, textIndexDefinition);
            log.info("Forms collection text search index created successfully.");
        }
    }
}
