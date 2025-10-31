package format.backend.form.dto;

import format.backend.form.entity.Language;
import java.time.Duration;
import org.springframework.lang.Nullable;

public record FormFilterDto(
        @Nullable String searchQuery,

        @Nullable Language language,

        @Nullable Duration minEstimatedDuration,

        @Nullable Duration maxEstimatedDuration) {}
