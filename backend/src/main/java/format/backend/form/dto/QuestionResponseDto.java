package format.backend.form.dto;

import format.backend.form.entity.QuestionType;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record QuestionResponseDto(
        @NonNull String id,

        @NonNull String content,

        @NonNull QuestionType type,

        @Nullable String image,

        @NonNull Boolean isRequired,

        @NonNull List<AnswerResponseDto> answers) {}
