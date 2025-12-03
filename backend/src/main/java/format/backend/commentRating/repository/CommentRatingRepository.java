package format.backend.commentRating.repository;

import format.backend.commentRating.entity.CommentRatingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRatingRepository extends MongoRepository<CommentRatingEntity, String> {}
