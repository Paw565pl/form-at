import { AnswerResponseDto } from "@/features/form-details/types/answer-response-dto";
import { QuestionType } from "@/features/form-details/types/question-type";

export interface QuestionResponseDto {
  id: string;
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerResponseDto[] | null;
}
