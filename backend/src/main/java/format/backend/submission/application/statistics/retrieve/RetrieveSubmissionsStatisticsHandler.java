package format.backend.submission.application.statistics.retrieve;

import format.backend.auth.UserClaims;
import format.backend.form.QuestionTypeView;
import format.backend.form.QuestionView;
import format.backend.submission.application.shared.SubmissionAccessGuard;
import format.backend.submission.domain.repository.SubmissionsStatisticsRepository;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveSubmissionsStatisticsHandler {

    private final SubmissionAccessGuard submissionAccessGuard;

    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;

    public RetrieveSubmissionsStatisticsResponseDto handle(UserClaims userClaims, String formIdOrSlug) {
        val formView = submissionAccessGuard.verifyAccessAndGetOrThrow(userClaims, formIdOrSlug);
        val submissionsStatisticsEntity =
                submissionsStatisticsRepository.findById(formView.id()).orElse(null);

        val questionsStatistics = formView.questions().stream()
                .filter(q -> q.type() != QuestionTypeView.OPEN)
                .collect(Collectors.toUnmodifiableMap(
                        QuestionView::id,
                        q -> new RetrieveSubmissionsStatisticsResponseDto.Statistics(q.answerIds().stream()
                                .collect(Collectors.toUnmodifiableMap(Function.identity(), answerId -> {
                                    if (submissionsStatisticsEntity == null) return 0L;

                                    val questionStatistics = submissionsStatisticsEntity
                                            .getQuestions()
                                            .get(q.id());
                                    if (questionStatistics == null) return 0L;

                                    val count = questionStatistics.getAnswers().getOrDefault(answerId, 0L);

                                    return Math.max(count, 0L);
                                })))));
        return new RetrieveSubmissionsStatisticsResponseDto(formView.submissionsCount(), questionsStatistics);
    }
}
