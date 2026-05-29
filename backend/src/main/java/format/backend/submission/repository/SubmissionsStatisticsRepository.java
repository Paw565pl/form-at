package format.backend.submission.repository;

import format.backend.submission.entity.SubmissionsStatisticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionsStatisticsRepository extends MongoRepository<SubmissionsStatisticsEntity, String> {}
