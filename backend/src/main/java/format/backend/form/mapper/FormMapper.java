package format.backend.form.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionResponseDto;
import format.backend.form.entity.FormEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = QuestionMapper.class)
public interface FormMapper {

    @Mapping(target = "questionsCount", expression = "java(formEntity.getQuestions().size())")
    FormListResponseDto toListResponseDto(FormEntity formEntity, String thumbnail);

    @Mapping(target = "questions", source = "questions")
    FormDetailResponseDto toDetailResponseDto(
            FormEntity formEntity, String thumbnail, List<QuestionResponseDto> questions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "language", expression = "java(formRequestDto.language().getMongoValue())")
    @Mapping(target = "submissionsCount", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    FormEntity toEntity(FormRequestDto formRequestDto, String slug, String passwordHash, UserEntity author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionsCount", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(
            FormRequestDto formRequestDto, @MappingTarget FormEntity entity, String slug, String passwordHash);
}
