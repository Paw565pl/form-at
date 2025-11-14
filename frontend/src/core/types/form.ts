import { QuestionResponseDto } from "@/core/types/question";
import { User } from "@/features/auth/types/user";

export enum FormStatus {
  Public = "PUBLIC", // visible to everyone
  Unpublic = "UNPUBLIC", // visible to people with the link
  Private = "PRIVATE", // accessed to people with password
  Draft = "DRAFT", // only visible to the author
  Closed = "CLOSED", // no longer accepting submissions
}

export enum Language {
  En = "EN",
  Pl = "PL",
}

export enum ShuffleVariant {
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
  estimatedDuration: string;
  thumbnailKey?: string;
  allowsQuestionsPreview: boolean; // allows previewing questions before submission
  allowsGuestSubmissions: boolean; // allows submissions from guests (not logged in users)
  saveSubmissions: boolean;
  authorId: string;
  author?: User;
  createdAt: Date;
  updatedAt: Date;
  submissionsCount: number;
  questions: QuestionResponseDto[];
}
