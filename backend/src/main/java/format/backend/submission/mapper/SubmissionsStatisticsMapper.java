package format.backend.submission.mapper;

import format.backend.submission.dto.SubmissionsStatisticsResponseDto;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionsStatisticsMapper {

    SubmissionsStatisticsResponseDto toDto(
            SubmissionsStatisticsEntity submissionsStatisticsEntity, Long submissionsCount);
}
