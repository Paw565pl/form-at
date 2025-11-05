package format.backend.form.mapper;

import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.dto.QuestionResponseDto;
import format.backend.form.entity.QuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AnswerMapper.class)
public interface QuestionMapper {

    QuestionResponseDto toResponseDto(QuestionEntity questionEntity, String image);

    @Mapping(target = "id", ignore = true)
    QuestionEntity toEntity(QuestionRequestDto questionRequestDto);
}
