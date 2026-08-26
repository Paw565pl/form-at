package format.backend.form.application.delete;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.core.exception.ForbiddenException;
import format.backend.form.FormDeletedEvent;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.QuestionEntity;
import format.backend.form.domain.exception.FormNotFoundException;
import format.backend.form.domain.repository.FormRatingRepository;
import format.backend.form.domain.repository.FormRepository;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteFormHandler {

    private final ApplicationEventPublisher eventPublisher;

    private final FormRepository formRepository;
    private final FormRatingRepository formRatingRepository;

    @Transactional
    public void handle(UserClaims userClaims, String idOrSlug) {
        val formEntity = formRepository.findByIdOrSlug(idOrSlug).orElseThrow(() -> new FormNotFoundException(idOrSlug));
        val formId = Objects.requireNonNull(formEntity.getId());

        val isAuthorOrAdmin = Objects.equals(formEntity.getAuthorId(), userClaims.id())
                || userClaims.roles().contains(Role.ADMIN);
        if (formEntity.getStatus() == FormStatus.CLOSED && !isAuthorOrAdmin) throw new FormNotFoundException(idOrSlug);
        if (!isAuthorOrAdmin) throw new ForbiddenException();

        formRepository.deleteById(formId);
        formRatingRepository.deleteAllByFormId(formId);

        val imageKeys = Stream.concat(
                        Stream.ofNullable(formEntity.getThumbnailKey()),
                        formEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        eventPublisher.publishEvent(new FormDeletedEvent(formId, imageKeys));
    }
}
