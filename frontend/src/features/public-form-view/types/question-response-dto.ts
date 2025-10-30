import { AnswerResponseDto } from "@/features/public-form-view/types/answer-response-dto";
import { QuestionType } from "@/features/public-form-view/types/question-type";

export interface QuestionResponseDto {
  id: string;
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerResponseDto[] | null;
}
