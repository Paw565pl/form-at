package format.backend.form;

import format.backend.auth.UserClaims;
import format.backend.form.application.shared.FormAccessGuard;
import format.backend.form.domain.entity.AnswerEntity;
import format.backend.form.domain.repository.FormRepository;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormFacade {

    private final FormRepository formRepository;
    private final FormAccessGuard accessGuard;

    public String resolveIdOrThrow(@Nullable UserClaims userClaims, String idOrSlug) {
        val formEntity = accessGuard.verifyAccessAndGetOrThrow(userClaims, idOrSlug);
        return Objects.requireNonNull(formEntity.getId());
    }

    public FormView getViewOrThrow(@Nullable UserClaims userClaims, String idOrSlug) {
        val formEntity = accessGuard.verifyAccessAndGetOrThrow(userClaims, idOrSlug);

        val questionViews = formEntity.getQuestions().stream()
                .map(q -> QuestionView.builder()
                        .id(q.getId())
                        .type(QuestionTypeView.fromQuestionType(q.getType()))
                        .isRequired(q.getIsRequired())
                        .answerIds(q.getAnswers().stream()
                                .map(AnswerEntity::getId)
                                .collect(Collectors.toUnmodifiableSet()))
                        .build())
                .toList();
        return FormView.builder()
                .id(Objects.requireNonNull(formEntity.getId()))
                .allowsGuestSubmissions(formEntity.getAllowsGuestSubmissions())
                .saveSubmissions(formEntity.getSaveSubmissions())
                .submissionsCount(formEntity.getSubmissionsCount())
                .authorId(formEntity.getAuthorId())
                .questions(questionViews)
                .build();
    }

    public void incrementSubmissionsCount(String id) {
        formRepository.updateSubmissionsCount(id, 1);
    }

    public void decrementSubmissionsCount(String id) {
        formRepository.updateSubmissionsCount(id, -1);
    }

    public long count() {
        return formRepository.count();
    }

    public long countByAuthorId(String authorId) {
        return formRepository.countAllByAuthorId(authorId);
    }
}
