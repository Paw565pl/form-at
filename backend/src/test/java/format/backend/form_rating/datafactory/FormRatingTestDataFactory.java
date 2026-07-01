package format.backend.form_rating.datafactory;

import format.backend.form_rating.entity.FormRatingEntity;

public abstract class FormRatingTestDataFactory {

    public static FormRatingEntity create(String formId, String authorId, Integer value) {
        var formRatingEntity = new FormRatingEntity(formId);
        formRatingEntity.setAuthorId(authorId);
        formRatingEntity.setValue(value);

        return formRatingEntity;
    }
}
