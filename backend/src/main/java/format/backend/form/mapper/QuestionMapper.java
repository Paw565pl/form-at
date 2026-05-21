package format.backend.form.mapper;

import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.dto.QuestionResponseDto;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.entity.QuestionType;
import org.jspecify.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AnswerMapper.class)
public interface QuestionMapper {

    QuestionResponseDto toResponseDto(QuestionEntity questionEntity, @Nullable String image);

    @Mapping(target = "id", ignore = true)
    QuestionEntity toEntity(QuestionRequestDto questionRequestDto);

    @AfterMapping
    default void clearOpenQuestionAnswers(@MappingTarget QuestionEntity questionEntity) {
        if (questionEntity.getType().equals(QuestionType.OPEN))
            questionEntity.getAnswers().clear();
    }
}
