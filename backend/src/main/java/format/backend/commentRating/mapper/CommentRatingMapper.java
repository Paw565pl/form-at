package format.backend.commentRating.mapper;

import format.backend.commentRating.dto.CommentRatingResponseDto;
import format.backend.commentRating.entity.CommentRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentRatingMapper {

    @Mapping(target = "commentId", source = "commentRating.comment.id")
    @Mapping(target = "type", expression = "java(RatingType.fromValue(commentRating.getType()))")
    CommentRatingResponseDto toResponseDto(CommentRatingEntity commentRating);
}
