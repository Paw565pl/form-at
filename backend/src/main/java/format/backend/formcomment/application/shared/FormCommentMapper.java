package format.backend.formcomment.application.shared;

import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.domain.repository.FormCommentListProjection;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FormCommentMapper {

    FormCommentResponseDto toResponseDto(
            FormCommentEntity commentEntity, @Nullable String authorName, @Nullable FormCommentRatingType userRating);

    FormCommentResponseDto toResponseDto(FormCommentListProjection commentListProjection);

    FormCommentEntity toEntity(FormCommentRequestDto requestDto, String formId, String authorId);
}
