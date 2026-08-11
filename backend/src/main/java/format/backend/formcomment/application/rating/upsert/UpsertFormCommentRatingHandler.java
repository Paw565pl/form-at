package format.backend.formcomment.application.rating.upsert;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.formcomment.FormCommentFacade;
import format.backend.formcomment.domain.repository.FormCommentRatingRepository;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpsertFormCommentRatingHandler {

    private final FormFacade formFacade;
    private final FormCommentFacade formCommentFacade;

    private final FormCommentRatingRepository formCommentRatingRepository;
    private final FormCommentRepository formCommentRepository;
    private final UpsertFormCommentRatingMapper formCommentRatingMapper;

    @Transactional
    public UpsertFormCommentRatingResponseDto handle(
            UserClaims userClaims,
            String formIdOrSlug,
            String formCommentId,
            UpsertFormCommentRatingRequestDto requestDto) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val validFormCommentId = formCommentFacade.resolveIdOrThrow(formCommentId, formId);
        val existingFormCommentRatingEntity = formCommentRatingRepository
                .findByCommentIdAndAuthorId(validFormCommentId, userClaims.id())
                .orElse(null);

        if (existingFormCommentRatingEntity != null) {
            val newRatingType = requestDto.type();
            val existingFormCommentRatingType = existingFormCommentRatingEntity.getType();
            if (Objects.equals(existingFormCommentRatingType, newRatingType)) {
                return formCommentRatingMapper.toResponseDto(existingFormCommentRatingEntity);
            }

            existingFormCommentRatingEntity.setType(newRatingType);
            val updatedFormCommentRatingEntity = formCommentRatingRepository.save(existingFormCommentRatingEntity);

            val ratingScoreDelta =
                    switch (newRatingType) {
                        case UPVOTE -> 2;
                        case DOWNVOTE -> -2;
                    };
            formCommentRepository.updateRatingScore(validFormCommentId, ratingScoreDelta);

            return formCommentRatingMapper.toResponseDto(updatedFormCommentRatingEntity);
        }

        val formCommentRatingEntity = formCommentRatingRepository.save(
                formCommentRatingMapper.toEntity(requestDto, formId, validFormCommentId, userClaims.id()));
        val ratingScoreDelta =
                switch (requestDto.type()) {
                    case UPVOTE -> 1;
                    case DOWNVOTE -> -1;
                };
        formCommentRepository.updateRatingScore(validFormCommentId, ratingScoreDelta);

        return formCommentRatingMapper.toResponseDto(formCommentRatingEntity);
    }
}
