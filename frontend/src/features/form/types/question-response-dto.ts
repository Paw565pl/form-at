import { AnswerResponseDto } from "@/features/form/types/answer-response-dto";
import { QuestionType } from "@/features/form/types/question-type";

export interface QuestionResponseDto {
  id: string;
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerResponseDto[] | null;
}
