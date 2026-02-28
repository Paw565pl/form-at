package format.backend.form.repository;

import format.backend.form.entity.FormEntity;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FormRepository extends MongoRepository<@NonNull FormEntity, @NonNull String> {
    Optional<FormEntity> findBySlug(String slug);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'submissionsCount': 1 } }")
    void incrementSubmissionsCount(String id);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'submissionsCount': -1 } }")
    void decrementSubmissionsCount(String id);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingsCount': 1 } }")
    void incrementRatingsCount(String id);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingsCount': -1 } }")
    void decrementRatingsCount(String id);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingsSum': ?1 } }")
    void updateRatingsSum(String id, int delta);

    int countAllByAuthorId(String authorId);
}
