package format.backend.form;

import java.util.Set;

public record FormQuestionsInvalidatedEvent(String id, Set<String> invalidatedQuestionIds) {
    public FormQuestionsInvalidatedEvent {
        invalidatedQuestionIds = Set.copyOf(invalidatedQuestionIds);
    }
}
