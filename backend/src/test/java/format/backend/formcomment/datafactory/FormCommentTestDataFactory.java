package format.backend.formcomment.datafactory;

import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;

public abstract class FormCommentTestDataFactory {

    public static FormCommentEntity create(String formId) {
        return create(formId, UserTestDataFactory.create().getId());
    }

    public static FormCommentEntity create(String formId, String authorId) {
        return FormCommentEntity.builder()
                .formId(formId)
                .authorId(authorId)
                .content("content")
                .build();
    }
}
