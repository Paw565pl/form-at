package format.backend.form.application.shared.dto;

import format.backend.form.domain.entity.QuestionType;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record QuestionResponseDto(
        String id,

        String content,

        QuestionType type,

        @Nullable String image,

        boolean isRequired,

        List<AnswerResponseDto> answers) {}
