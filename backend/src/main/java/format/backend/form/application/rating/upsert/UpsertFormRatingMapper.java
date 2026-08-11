package format.backend.form.application.rating.upsert;

import format.backend.form.domain.entity.FormRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UpsertFormRatingMapper {

    UpsertFormRatingResponseDto toResponseDto(FormRatingEntity entity);

    FormRatingEntity toEntity(UpsertFormRatingRequestDto dto, String formId, String authorId);
}
