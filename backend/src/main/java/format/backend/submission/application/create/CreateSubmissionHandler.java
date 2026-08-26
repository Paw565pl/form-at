package format.backend.submission.application.create;

import format.backend.auth.UserClaims;
import format.backend.core.exception.UnauthorizedException;
import format.backend.core.exception.ValidationException;
import format.backend.form.FormFacade;
import format.backend.form.QuestionView;
import format.backend.submission.application.shared.dto.SubmissionRequestDto;
import format.backend.submission.application.shared.dto.SubmissionResponseDto;
import format.backend.submission.application.shared.mapper.SubmissionMapper;
import format.backend.submission.domain.entity.SubmissionAnswerEntity;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.exception.SubmissionAlreadyCreatedForUserException;
import format.backend.submission.domain.exception.SubmissionOperationNotSupported;
import format.backend.submission.domain.repository.SubmissionRepository;
import format.backend.submission.domain.repository.SubmissionsStatisticsRepository;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSubmissionHandler {

    private final FormFacade formFacade;

    private final SubmissionRepository submissionRepository;
    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;
    private final SubmissionMapper submissionMapper;
    private final CreateSubmissionValidator submissionValidator;

    @Transactional
    public SubmissionResponseDto handle(
            @Nullable UserClaims userClaims, String formIdOrSlug, SubmissionRequestDto requestDto) {
        val formView = formFacade.getViewOrThrow(userClaims, formIdOrSlug);

        if (!formView.allowsGuestSubmissions() && userClaims == null) {
            throw new UnauthorizedException();
        }
        if (!formView.saveSubmissions() || formView.authorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val errors = submissionValidator.validate(formView, requestDto);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val questionViewsById = formView.questions().stream()
                .collect(Collectors.toUnmodifiableMap(QuestionView::id, Function.identity()));
        val submissionAnswerEntities = new ArrayList<SubmissionAnswerEntity>();
        for (val submissionAnswer : requestDto.answers()) {
            val question = questionViewsById.get(submissionAnswer.questionId());
            if (question == null) continue;

            switch (question.type()) {
                case SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                    val existingAnswerIds = submissionAnswer.chosenAnswerIds().stream()
                            .filter(answerId -> question.answerIds().contains(answerId))
                            .collect(Collectors.toUnmodifiableSet());
                    submissionAnswerEntities.add(
                            SubmissionAnswerEntity.forQuestionWithAnswers(question.id(), existingAnswerIds));
                }
                case OPEN ->
                    submissionAnswerEntities.add(SubmissionAnswerEntity.forOpenQuestion(
                            question.id(), Objects.requireNonNull(submissionAnswer.openAnswer())));
            }
        }

        final SubmissionEntity submissionEntity;
        try {
            submissionEntity = submissionRepository.save(SubmissionEntity.builder()
                    .formId(formView.id())
                    .authorId(userClaims != null ? userClaims.id() : null)
                    .answers(submissionAnswerEntities)
                    .build());
        } catch (DataIntegrityViolationException _) {
            throw new SubmissionAlreadyCreatedForUserException(formIdOrSlug);
        }

        submissionsStatisticsRepository.update(submissionEntity, 1);
        formFacade.incrementSubmissionsCount(formView.id());
        val authorName = userClaims != null ? userClaims.username() : null;

        return submissionMapper.toResponseDto(submissionEntity, authorName);
    }
}
