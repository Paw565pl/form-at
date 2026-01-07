export interface SubmissionRequestDto {
  answers: SubmissionAnswerDto[];
}

export interface SubmissionAnswerDto {
  questionId: string;
  chosenAnswerIds: string[];
  openAnswer: string | null;
}

export interface SubmissionResponseDto {
  id: string;
  answers: SubmissionAnswerDto[];
  createdAt: string;
}
