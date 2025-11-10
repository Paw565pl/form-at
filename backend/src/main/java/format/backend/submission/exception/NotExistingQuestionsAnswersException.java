package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import java.util.List;
import org.springframework.http.HttpStatus;

public final class NotExistingQuestionsAnswersException extends ApplicationException {

    public NotExistingQuestionsAnswersException(String formIdOrSlug, List<String> nonExistingQuestionIds) {
        super(
                HttpStatus.BAD_REQUEST,
                String.format(
                        "Form with id or slug '%s' does not have questions with ids %s",
                        formIdOrSlug, String.join(", ", nonExistingQuestionIds)));
    }
}
