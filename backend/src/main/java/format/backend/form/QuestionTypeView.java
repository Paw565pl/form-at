package format.backend.form;

import format.backend.form.domain.entity.QuestionType;

public enum QuestionTypeView {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    OPEN;

    static QuestionTypeView fromQuestionType(QuestionType questionType) {
        return switch (questionType) {
            case SINGLE_CHOICE -> SINGLE_CHOICE;
            case MULTIPLE_CHOICE -> MULTIPLE_CHOICE;
            case OPEN -> OPEN;
        };
    }
}
