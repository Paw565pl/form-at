export interface SubmissionStatisticsResponseDto {
  readonly questionId: string;
  readonly submissionStatistics: SubmissionAnswersStatisticsResponseDto[];
}

export interface SubmissionAnswersStatisticsResponseDto {
  readonly answerId: string;
  readonly totalCount: number;
}
