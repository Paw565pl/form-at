package format.backend.submission.mapper;

import format.backend.submission.dto.SubmissionAnswersStatisticsResponseDto;
import format.backend.submission.dto.SubmissionStatisticsResponseDto;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import java.util.List;
import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionsStatisticsMapper {

    default List<SubmissionStatisticsResponseDto> toResponseDtos(
            SubmissionsStatisticsEntity submissionsStatisticsEntity) {
        return submissionsStatisticsEntity.getQuestions().entrySet().stream()
                .map((entry) -> {
                    val questionId = entry.getKey();
                    val submissionsStatistics = entry.getValue();

                    var submissionAnswersStatisticsResponseDtos = submissionsStatistics.getAnswers().entrySet().stream()
                            .map(statisticsEntry -> new SubmissionAnswersStatisticsResponseDto(
                                    statisticsEntry.getKey(), statisticsEntry.getValue()))
                            .toList();

                    return new SubmissionStatisticsResponseDto(questionId, submissionAnswersStatisticsResponseDtos);
                })
                .toList();
    }
}
