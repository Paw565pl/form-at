package format.backend.form.repository;

import format.backend.form.entity.FormEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormRepository extends MongoRepository<FormEntity, String> {
    Optional<FormEntity> findBySlug(String slug);
}
