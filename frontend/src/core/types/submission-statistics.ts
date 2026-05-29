export interface SubmissionsStatisticsResponseDto {
  readonly submissionsCount: number;
  readonly questions: Readonly<Record<string, Statistics>>;
}

interface Statistics {
  readonly answers: Readonly<Record<string, number>>;
}
