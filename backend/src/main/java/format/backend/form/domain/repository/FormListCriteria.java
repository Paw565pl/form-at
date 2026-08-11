package format.backend.form.domain.repository;

import format.backend.form.domain.entity.FormLanguage;
import java.time.Duration;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record FormListCriteria(
        @Nullable String searchQuery,

        @Nullable FormLanguage language,

        @Nullable Duration minEstimatedDuration,

        @Nullable Duration maxEstimatedDuration,

        @Nullable Boolean allowsGuestSubmissions,

        @Nullable String authorId) {}
