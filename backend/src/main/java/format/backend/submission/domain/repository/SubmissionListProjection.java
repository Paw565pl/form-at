package format.backend.submission.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record SubmissionListProjection(
        String id, @Nullable String authorName, List<SubmissionAnswerListProjection> answers, Instant createdAt) {
    public record SubmissionAnswerListProjection(
            String questionId,

            Set<String> chosenAnswerIds,

            @Nullable String openAnswer) {}
}
