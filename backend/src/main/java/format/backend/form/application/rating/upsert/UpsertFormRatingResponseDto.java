package format.backend.form.application.rating.upsert;

import java.time.Instant;

public record UpsertFormRatingResponseDto(String id, int value, Instant createdAt, Instant updatedAt) {}
