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
  readonly id: string;
  readonly name: string;
  readonly slug: string;
  readonly description?: string;
  readonly language: Language;
  readonly status: FormStatus;
  readonly shuffleVariant?: ShuffleVariant;
  readonly thanksMessage?: string;
  readonly estimatedDuration: string;
  readonly thumbnailKey?: string;
  readonly allowsQuestionsPreview: boolean; // allows previewing questions before submission
  readonly allowsGuestSubmissions: boolean; // allows submissions from guests (not logged in users)
  readonly saveSubmissions: boolean;
  readonly authorId: string;
  readonly author?: User;
  readonly createdAt: Date;
  readonly updatedAt: Date;
  readonly submissionsCount: number;
  readonly questions: QuestionResponseDto[];
}
