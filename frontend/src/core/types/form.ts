import { QuestionResponseDto } from "@/core/types/question";
import { User } from "@/features/auth/types/user";

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
  shuffleVariant?: ShuffleVariant;
  thanksMessage?: string;
  estimatedDuration: number;
  thumbnailKey?: string;
  allowsQuestionsPreview: boolean;
  allowsGuestSubmissions: boolean;
  saveSubmissions: boolean;
  authorId: string;
  author?: User;
  createdAt: Date;
  updatedAt: Date;
  submissionsCount: number;
  questions: QuestionResponseDto[];
}
