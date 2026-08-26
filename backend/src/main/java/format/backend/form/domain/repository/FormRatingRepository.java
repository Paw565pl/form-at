package format.backend.form.domain.repository;

import format.backend.form.domain.entity.FormRatingEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormRatingRepository extends MongoRepository<FormRatingEntity, String> {

    Optional<FormRatingEntity> findByFormIdAndAuthorId(String formId, String authorId);

    long deleteAllByFormId(String formId);
}
