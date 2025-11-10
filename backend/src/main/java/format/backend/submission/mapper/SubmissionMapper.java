package format.backend.submission.mapper;

import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.entity.SubmissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionMapper {

    SubmissionResponseDto toResponseDto(SubmissionEntity submissionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "form", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SubmissionEntity toEntity(SubmissionRequestDto submissionRequestDto);
}
