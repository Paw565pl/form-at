package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import java.util.List;
import org.springframework.http.HttpStatus;

public final class RequiredQuestionsNotAnsweredException extends ApplicationException {

    public RequiredQuestionsNotAnsweredException(List<String> notAnsweredRequiredQuestionIds) {
        super(
                HttpStatus.BAD_REQUEST,
                String.format(
                        "Required questions with ids %s were not answered",
                        String.join(", ", notAnsweredRequiredQuestionIds)));
    }
}
