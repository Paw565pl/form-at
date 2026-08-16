package format.backend.form.application.shared.mapper;

import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.application.shared.dto.QuestionResponseDto;
import format.backend.form.domain.entity.QuestionEntity;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {

    QuestionResponseDto toResponseDto(QuestionEntity questionEntity, @Nullable String image);

    @Mapping(target = "answer", ignore = true)
    QuestionEntity toEntity(QuestionRequestDto requestDto);
}
