package format.backend.form.application.list;

import format.backend.form.domain.repository.FormListProjection;
import java.time.Duration;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ListFormsMapper {

    @Mapping(target = "estimatedDuration", source = "formListProjection.estimatedDurationSeconds")
    @Mapping(target = "ratingAvg", source = "formListProjection")
    ListFormsResponseDto toResponseDto(FormListProjection formListProjection, @Nullable String thumbnail);

    static Duration mapEstimatedDuration(long estimatedDurationSeconds) {
        return Duration.ofSeconds(estimatedDurationSeconds);
    }

    static @Nullable Double mapRatingAvg(FormListProjection formListProjection) {
        return formListProjection.ratingsCount() == 0
                ? null
                : (double) formListProjection.ratingsSum() / formListProjection.ratingsCount();
    }
}
