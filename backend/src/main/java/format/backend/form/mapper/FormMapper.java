package format.backend.form.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionResponseDto;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormListAggregationResult;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = QuestionMapper.class)
public interface FormMapper {

    FormListResponseDto toListResponseDto(FormEntity formEntity, String thumbnail, @Nullable String authorName);

    FormListResponseDto toListResponseDto(FormListAggregationResult formListAggregationResult, String thumbnail);

    @Mapping(target = "questions", source = "questions")
    FormDetailResponseDto toDetailResponseDto(
            FormEntity formEntity, String thumbnail, @Nullable String authorName, List<QuestionResponseDto> questions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "language", expression = "java(formRequestDto.language().getMongoValue())")
    @Mapping(
            target = "estimatedDurationSeconds",
            expression = "java((int) formRequestDto.estimatedDuration().toSeconds())")
    @Mapping(target = "questionsCount", expression = "java(formRequestDto.questions().size())")
    @Mapping(target = "submissionsCount", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    FormEntity toEntity(FormRequestDto formRequestDto, String slug, String passwordHash, @Nullable UserEntity author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "questionsCount", expression = "java(formRequestDto.questions().size())")
    @Mapping(target = "submissionsCount", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    FormEntity updateEntityFromDto(
            FormRequestDto formRequestDto, @MappingTarget FormEntity entity, String slug, String passwordHash);
}
