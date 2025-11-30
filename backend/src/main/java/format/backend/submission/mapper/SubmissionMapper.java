package format.backend.submission.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.entity.SubmissionEntity;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionMapper {

    SubmissionResponseDto toResponseDto(SubmissionEntity submissionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "form", source = "form")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "createdAt", ignore = true)
    SubmissionEntity toEntity(SubmissionRequestDto submissionRequestDto, FormEntity form, @Nullable UserEntity author);
}
