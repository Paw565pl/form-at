package format.backend.submission.mapper;

import format.backend.submission.dto.SubmissionAnswerRequestDto;
import format.backend.submission.dto.SubmissionAnswerResponseDto;
import format.backend.submission.entity.SubmissionAnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionAnswerMapper {

    SubmissionAnswerResponseDto toResponseDto(SubmissionAnswerEntity submissionAnswerEntity);

    SubmissionAnswerEntity toEntity(SubmissionAnswerRequestDto submissionAnswerRequestDto);
}
