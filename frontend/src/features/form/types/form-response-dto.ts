import { QuestionResponseDto } from "@/features/form/types/question-response-dto";

export enum FormStatus {
  Public = "PUBLIC",
  Unpublic = "UNPUBLIC",
  Private = "PRIVATE",
  Draft = "DRAFT",
  Closed = "CLOSED",
}

export enum Language {
  En = "EN",
  Pl = "PL",
}

export enum ShuffleVariant {
  Null = "NULL",
  Questions = "QUESTIONS",
  Answers = "ANSWERS",
  Both = "BOTH",
}

export interface FormResponseDto {
  id: string;
  name: string;
  slug: string;
  description?: string;
  language: Language;
  status: FormStatus;
  shuffleVariant: ShuffleVariant;
  thanksMessage: string;
  estimatedDuration: number;
  thumbnailKey: string;
  allowsQuestionPreview: boolean;
  allowsGuestSubmissions: boolean;
  saveSubmissions: boolean;
  authorId: string;
  createdAt: string;
  updatedAt: string;
  submissionCount: number;
  questions: QuestionResponseDto[];
}
