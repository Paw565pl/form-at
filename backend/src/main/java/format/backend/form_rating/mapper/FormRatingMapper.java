package format.backend.form_rating.mapper;

import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.dto.FormRatingResponseDto;
import format.backend.form_rating.entity.FormRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FormRatingMapper {
    FormRatingResponseDto toResponseDto(FormRatingEntity formRating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "value", source = "formRatingRequestDto.ratingValue")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    FormRatingEntity toEntity(FormRatingRequestDto formRatingRequestDto, String formId, String authorId);
}
