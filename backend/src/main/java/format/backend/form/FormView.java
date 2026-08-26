package format.backend.form;

import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record FormView(
        String id,
        boolean allowsGuestSubmissions,
        boolean saveSubmissions,
        long submissionsCount,
        @Nullable String authorId,
        List<QuestionView> questions) {
    public FormView {
        questions = List.copyOf(questions);
    }
}
