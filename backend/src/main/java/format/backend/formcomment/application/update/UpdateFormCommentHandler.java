package format.backend.formcomment.application.update;

import format.backend.auth.UserClaims;
import format.backend.core.exception.ForbiddenException;
import format.backend.form.FormFacade;
import format.backend.formcomment.application.shared.FormCommentMapper;
import format.backend.formcomment.application.shared.FormCommentRequestDto;
import format.backend.formcomment.application.shared.FormCommentResponseDto;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.exception.FormCommentNotFoundException;
import format.backend.formcomment.domain.repository.FormCommentRatingRepository;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateFormCommentHandler {

    private final FormFacade formFacade;

    private final FormCommentRepository formCommentRepository;
    private final FormCommentRatingRepository formCommentRatingRepository;
    private final FormCommentMapper formCommentMapper;

    public FormCommentResponseDto handle(
            UserClaims userClaims, String formIdOrSlug, String formCommentId, FormCommentRequestDto requestDto) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val formCommentEntity = formCommentRepository
                .findById(formCommentId)
                .orElseThrow(() -> new FormCommentNotFoundException(formCommentId));

        if (!Objects.equals(formId, formCommentEntity.getFormId())) {
            throw new FormCommentNotFoundException(formCommentId);
        }
        if (!Objects.equals(formCommentEntity.getAuthorId(), userClaims.id())) {
            throw new ForbiddenException();
        }

        formCommentEntity.setContent(requestDto.content());
        val updatedFormCommentEntity = formCommentRepository.save(formCommentEntity);

        val userRating = formCommentRatingRepository
                .findByCommentIdAndAuthorId(formCommentId, userClaims.id())
                .map(FormCommentRatingEntity::getType)
                .orElse(null);
        return formCommentMapper.toResponseDto(updatedFormCommentEntity, userClaims.username(), userRating);
    }
}
