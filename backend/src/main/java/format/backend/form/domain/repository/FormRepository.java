package format.backend.form.domain.repository;

import format.backend.form.domain.entity.FormEntity;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FormRepository extends MongoRepository<FormEntity, String>, FormRepositoryCustom {
    Optional<FormEntity> findBySlug(String slug);

    default Optional<FormEntity> findByIdOrSlug(String idOrSlug) {
        return ObjectId.isValid(idOrSlug) ? findById(idOrSlug) : findBySlug(idOrSlug);
    }

    @Query("{ _id: ?0 }")
    @Update("{ $inc: { 'ratingsCount': ?1, 'ratingsSum': ?2 } }")
    long updateRatingFields(String id, long ratingsCountDelta, long ratingsSumDelta);

    @Query("{ _id: ?0 }")
    @Update("{ $inc: { 'submissionsCount': ?1 } }")
    long updateSubmissionsCount(String id, long submissionsCountDelta);

    long countAllByAuthorId(String authorId);
}
