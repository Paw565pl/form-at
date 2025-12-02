export interface AnswerResponseDto {
  readonly id: string;
  readonly content: string;
  readonly isCorrect: boolean;
}

export interface AnswerRequestDto {
  content: string;
  isCorrect: boolean;
}
