package format.backend.form.dto;

import org.springframework.lang.NonNull;

public record AnswerResponseDto(
        @NonNull String id,

        @NonNull String content,

        @NonNull Boolean isCorrect) {}
