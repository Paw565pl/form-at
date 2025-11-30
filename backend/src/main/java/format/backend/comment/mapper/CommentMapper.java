package format.backend.comment.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.form.entity.FormEntity;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "authorName")
    CommentResponseDto toResponseDto(CommentEntity comment, String authorName);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "form", source = "form")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    CommentEntity toEntity(CommentRequestDto commentRequestDto, FormEntity form, @Nullable UserEntity author);
}
