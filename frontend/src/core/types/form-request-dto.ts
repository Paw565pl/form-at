import {
  FormShuffleVariant,
  FormStatus,
  Language,
  QuestionType,
} from "@/core/types/form-detail-response-dto";

export interface FormRequestDto {
  name: string;
  description: string | null;
  language: Language;
  status: FormStatus;
  password: string | null;
  shuffleVariant: FormShuffleVariant | null;
  thanksMessage: string | null;
  estimatedDuration: string;
  thumbnailKey: string | null;
  allowsQuestionsPreview: boolean;
  allowsGuestSubmissions: boolean;
  saveSubmissions: boolean;
  questions: QuestionRequestDto[];
}

export interface QuestionRequestDto {
  content: string;
  type: QuestionType;
  imageKey: string | null;
  isRequired: boolean;
  answers: AnswerRequestDto[];
}

export interface AnswerRequestDto {
  content: string;
  isCorrect: boolean;
}
