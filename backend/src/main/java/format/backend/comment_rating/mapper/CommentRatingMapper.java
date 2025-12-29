package format.backend.comment_rating.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.dto.CommentRatingRequestDto;
import format.backend.comment_rating.dto.CommentRatingResponseDto;
import format.backend.comment_rating.entity.CommentRatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentRatingMapper {
    
    @Mapping(target = "type", expression = "java(RatingType.fromValue(commentRating.getType()))")
    CommentRatingResponseDto toResponseDto(CommentRatingEntity commentRating);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "type", expression = "java(commentRequestDto.type().getValue())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    CommentRatingEntity toEntity(CommentRatingRequestDto commentRequestDto, CommentEntity comment, UserEntity author);
}
