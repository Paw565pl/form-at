package format.backend.formcomment.datafactory;

import format.backend.formcomment.domain.entity.FormCommentEntity;

public abstract class FormCommentTestDataFactory {

    public static FormCommentEntity create(String formId) {
        return create(formId, null);
    }

    public static FormCommentEntity create(String formId, String authorId) {
        return FormCommentEntity.builder()
                .formId(formId)
                .authorId(authorId)
                .content("content")
                .build();
    }
}
