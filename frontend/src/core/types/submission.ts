export interface SubmissionRequestDto {
  answers: SubmissionAnswerRequestDto[];
}

export interface SubmissionAnswerRequestDto {
  questionId: string;
  chosenAnswerIds: string[];
  openAnswer: string | null;
}

export interface SubmissionResponseDto {
  readonly id: string;
  readonly authorName: string | null;
  readonly answers: SubmissionAnswerResponseDto[];
  readonly createdAt: string;
}

export interface SubmissionAnswerResponseDto {
  readonly questionId: string;
  readonly chosenAnswerIds: string[];
  readonly openAnswer: string | null;
}
