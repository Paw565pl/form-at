package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    Page<CommentEntity> findAllByFormId(String formId, Pageable pageable);

    void deleteAllByFormId(String formId);
}
