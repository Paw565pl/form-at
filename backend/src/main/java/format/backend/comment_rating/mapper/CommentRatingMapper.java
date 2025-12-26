package format.backend.comment_rating.mapper;

import format.backend.comment_rating.dto.CommentRatingResponseDto;
import format.backend.comment_rating.entity.CommentRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentRatingMapper {

    @Mapping(target = "commentId", source = "commentRating.comment.id")
    @Mapping(target = "type", expression = "java(RatingType.fromValue(commentRating.getType()))")
    CommentRatingResponseDto toResponseDto(CommentRatingEntity commentRating);
}
