import { AnswerResponseDto } from "./answer-response-dto";
import { QuestionType } from "./question-type";

export interface QuestionResponseDto {
  id: string;
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerResponseDto[] | null;
}
