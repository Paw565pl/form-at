package format.backend.form.mapper;

import format.backend.form.dto.AnswerRequestDto;
import format.backend.form.dto.AnswerResponseDto;
import format.backend.form.entity.AnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AnswerMapper {

    AnswerResponseDto toResponseDto(AnswerEntity answerEntity);

    @Mapping(target = "id", ignore = true)
    AnswerEntity toEntity(AnswerRequestDto answerRequestDto);
}
