package format.backend.submission.mapper;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.entity.SubmissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = SubmissionAnswerMapper.class)
public interface SubmissionMapper {

    SubmissionResponseDto toResponseDto(SubmissionEntity submissionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SubmissionEntity toEntity(SubmissionRequestDto submissionRequestDto, FormEntity form, UserEntity author);
}
