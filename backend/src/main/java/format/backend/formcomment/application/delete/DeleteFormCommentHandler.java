package format.backend.formcomment.application.delete;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.core.exception.ForbiddenException;
import format.backend.form.FormFacade;
import format.backend.formcomment.domain.exception.FormCommentNotFoundException;
import format.backend.formcomment.domain.repository.FormCommentRatingRepository;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteFormCommentHandler {

    private final FormFacade formFacade;

    private final FormCommentRepository formCommentRepository;
    private final FormCommentRatingRepository formCommentRatingRepository;

    @Transactional
    public void handle(UserClaims userClaims, String formIdOrSlug, String formCommentId) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val formCommentEntity = formCommentRepository
                .findById(formCommentId)
                .orElseThrow(() -> new FormCommentNotFoundException(formCommentId));

        if (!Objects.equals(formId, formCommentEntity.getFormId())) {
            throw new FormCommentNotFoundException(formCommentId);
        }

        val isAuthorOrAdmin = Objects.equals(formCommentEntity.getAuthorId(), userClaims.id())
                || userClaims.roles().contains(Role.ADMIN);
        if (!isAuthorOrAdmin) throw new ForbiddenException();

        formCommentRepository.deleteById(formCommentId);
        formCommentRatingRepository.deleteAllByCommentId(formCommentId);
    }
}
