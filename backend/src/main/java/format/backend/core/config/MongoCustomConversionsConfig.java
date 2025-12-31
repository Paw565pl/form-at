package format.backend.core.config;

import format.backend.comment.converter.CommentResponseDtoConverter;
import format.backend.form.converter.LanguageReadConverter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
class MongoCustomConversionsConfig {

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new LanguageReadConverter(), new CommentResponseDtoConverter()));
    }
}
