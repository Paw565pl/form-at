package format.backend.form.rating.datafactory;

import format.backend.form.domain.entity.FormRatingEntity;

public abstract class FormRatingTestDataFactory {

    public static FormRatingEntity create(String formId, String authorId, Integer value) {
        return FormRatingEntity.builder()
                .formId(formId)
                .authorId(authorId)
                .value(value)
                .build();
    }
}
