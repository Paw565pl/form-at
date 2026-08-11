package format.backend.formcomment.rating.datafactory;

import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import lombok.Builder;

public abstract class FormCommentRatingTestDataFactory {

    @Builder(builderMethodName = "createWithDefaults")
    public static FormCommentRatingEntity create(
            String formId, String commentId, String authorId, FormCommentRatingType type) {
        return FormCommentRatingEntity.builder()
                .formId(formId)
                .commentId(commentId)
                .authorId(authorId)
                .type(type)
                .build();
    }
}
