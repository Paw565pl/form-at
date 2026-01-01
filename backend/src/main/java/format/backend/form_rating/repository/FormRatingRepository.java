package format.backend.form_rating.repository;

import format.backend.form_rating.entity.FormRatingEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormRatingRepository extends MongoRepository<@NonNull FormRatingEntity, @NonNull String> {}
