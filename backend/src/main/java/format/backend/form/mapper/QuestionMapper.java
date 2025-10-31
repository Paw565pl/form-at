package format.backend.form.mapper;

import format.backend.form.dto.QuestionResponseDto;
import format.backend.form.entity.QuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {

    QuestionResponseDto toResponseDto(QuestionEntity questionEntity, String image);
}
