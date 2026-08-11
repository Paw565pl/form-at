package format.backend.formcomment.application.rating.upsert;

import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UpsertFormCommentRatingMapper {

    UpsertFormCommentRatingResponseDto toResponseDto(FormCommentRatingEntity formCommentRatingEntity);

    FormCommentRatingEntity toEntity(
            UpsertFormCommentRatingRequestDto requestDto, String formId, String commentId, String authorId);
}
