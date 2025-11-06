import { AnswerResponseDto } from "@/core/types/answer";

export enum QuestionType {
  Single = "SINGLE",
  Multiple = "MULTIPLE",
  Open = "OPEN",
}

export interface QuestionResponseDto {
  id: string;
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerResponseDto[] | null;
}
