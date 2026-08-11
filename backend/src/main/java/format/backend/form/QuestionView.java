package format.backend.form;

import java.util.Set;
import lombok.Builder;

@Builder
public record QuestionView(String id, QuestionTypeView type, boolean isRequired, Set<String> answerIds) {
    public QuestionView {
        answerIds = Set.copyOf(answerIds);
    }
}
