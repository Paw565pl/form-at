package format.backend.form.repository;

import format.backend.form.entity.FormEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FormRepository extends MongoRepository<FormEntity, String> {
    Optional<FormEntity> findBySlug(String slug);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'submissionsCount': 1 } }")
    void incrementSubmissionsCount(String id);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'submissionsCount': -1 } }")
    void decrementSubmissionsCount(String id);
}
