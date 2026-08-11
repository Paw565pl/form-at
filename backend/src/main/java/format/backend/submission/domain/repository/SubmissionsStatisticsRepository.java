package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionsStatisticsRepository
        extends MongoRepository<SubmissionsStatisticsEntity, String>, SubmissionsStatisticsRepositoryCustom {

    long deleteAllByFormId(String formId);
}
