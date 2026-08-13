package format.backend.form.application.shared.mapper;

import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.FormResponseDto;
import format.backend.form.application.shared.dto.QuestionResponseDto;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.QuestionEntity;
import java.time.Duration;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FormMapper {

    @Mapping(target = "estimatedDuration", source = "formEntity.estimatedDurationSeconds")
    @Mapping(target = "questions", source = "questions")
    @Mapping(target = "ratingAvg", source = "formEntity")
    FormResponseDto toResponseDto(
            FormEntity formEntity,
            @Nullable String thumbnail,
            List<QuestionResponseDto> questions,
            @Nullable Integer userRating,
            @Nullable String authorName);

    static Duration mapEstimatedDuration(long estimatedDurationSeconds) {
        return Duration.ofSeconds(estimatedDurationSeconds);
    }

    static @Nullable Double mapRatingAvg(FormEntity formEntity) {
        return formEntity.getRatingAvg();
    }

    @Mapping(target = "thumbnailKey", source = "thumbnailKey")
    @Mapping(target = "questions", source = "questions")
    @Mapping(target = "question", ignore = true)
    FormEntity toEntity(
            FormRequestDto requestDto,
            String slug,
            @Nullable String passwordHash,
            @Nullable String thumbnailKey,
            List<QuestionEntity> questions,
            String authorId);
}
