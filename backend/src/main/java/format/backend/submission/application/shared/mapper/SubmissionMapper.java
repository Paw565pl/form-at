package format.backend.submission.application.shared.mapper;

import format.backend.submission.application.shared.dto.SubmissionResponseDto;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.repository.SubmissionListProjection;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionMapper {

    SubmissionResponseDto toResponseDto(SubmissionListProjection submissionListProjection);

    SubmissionResponseDto toResponseDto(SubmissionEntity submissionEntity, @Nullable String authorName);
}
