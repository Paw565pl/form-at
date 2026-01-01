package format.backend.form_rating.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.dto.FormRatingResponseDto;
import format.backend.form_rating.entity.FormRatingEntity;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FormRatingMapper {
    FormRatingResponseDto toResponseDto(FormRatingEntity formRating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "form", source = "form")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    FormRatingEntity toEntity(FormRatingRequestDto formRatingRequestDto, FormEntity form, @Nullable UserEntity author);
}
