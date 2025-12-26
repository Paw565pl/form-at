import { AnswerRequestDto, AnswerResponseDto } from "@/core/types/answer";

export interface QuestionResponseDto {
  readonly id: string;
  readonly content: string;
  readonly type: QuestionType;
  readonly image: string | null;
  readonly isRequired: boolean;
  readonly answers: AnswerResponseDto[];
}

export enum QuestionType {
  SingleChoice = "SINGLE_CHOICE",
  MultipleChoice = "MULTIPLE_CHOICE",
  Open = "OPEN",
}

export interface QuestionRequestDto {
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerRequestDto[];
}

export interface QuestionRequest extends Omit<QuestionRequestDto, "imageKey"> {
  image: File | null;
}
