import { AnswerResponseDto } from "@/core/types/answer";

export enum QuestionType {
  Single = "SINGLE",
  Multiple = "MULTIPLE",
  Open = "OPEN",
}

export interface QuestionResponseDto {
  readonly id: string;
  readonly content: string;
  readonly type: QuestionType;
  readonly imageKey?: string;
  readonly isRequired: boolean;
  readonly answers: AnswerResponseDto[];
}
