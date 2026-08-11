package format.backend.formcomment.application.rating.delete;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.formcomment.FormCommentFacade;
import format.backend.formcomment.domain.exception.FormCommentNotRatedByUserException;
import format.backend.formcomment.domain.repository.FormCommentRatingRepository;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteFormCommentRatingHandler {

    private final FormFacade formFacade;
    private final FormCommentFacade formCommentFacade;

    private final FormCommentRatingRepository formCommentRatingRepository;
    private final FormCommentRepository formCommentRepository;

    @Transactional
    public void handle(UserClaims userClaims, String formIdOrSlug, String formCommentId) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val validFormCommentId = formCommentFacade.resolveIdOrThrow(formCommentId, formId);
        val formCommentRatingEntity = formCommentRatingRepository
                .findByCommentIdAndAuthorId(validFormCommentId, userClaims.id())
                .orElseThrow(() -> new FormCommentNotRatedByUserException(validFormCommentId));

        formCommentRatingRepository.deleteById(Objects.requireNonNull(formCommentRatingEntity.getId()));

        val ratingScoreDelta =
                switch (formCommentRatingEntity.getType()) {
                    case UPVOTE -> -1;
                    case DOWNVOTE -> 1;
                };
        formCommentRepository.updateRatingScore(validFormCommentId, ratingScoreDelta);
    }
}
