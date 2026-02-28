package format.backend.comment.mapper;

import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.entity.RatingType;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "authorName")
    @Mapping(target = "userRating", source = "userRating")
    CommentResponseDto toResponseDto(CommentEntity comment, String authorName, @Nullable RatingType userRating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    CommentEntity toEntity(CommentRequestDto commentRequestDto, String formId, String authorId);
}
