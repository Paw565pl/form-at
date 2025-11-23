import { QuestionRequestDto, QuestionResponseDto } from "@/core/types/question";
import { SortOptionsDto } from "@/core/types/sort-options-dto";

export interface FormListResponseDto {
  readonly id: string;
  readonly name: string;
  readonly slug: string;
  readonly description: string | null;
  readonly language: string;
  readonly status: string;
  readonly estimatedDuration: string;
  readonly thumbnail: string | null;
  readonly allowsQuestionsPreview: boolean;
  readonly allowsGuestSubmissions: boolean;
  readonly saveSubmissions: boolean;
  readonly authorName: string | null;
  readonly submissionsCount: number;
  readonly questionsCount: number;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface FormFilterOptionsDto {
  searchQuery?: string | null;
  language?: Language | null;
  minEstimatedDuration?: string | null;
  maxEstimatedDuration?: string | null;
  allowsGuestSubmissions?: boolean | null;
}

export const formSortOptions = {
  "estimatedDuration:asc": new SortOptionsDto("estimatedDuration", "asc"),
  "estimatedDuration:desc": new SortOptionsDto("estimatedDuration", "desc"),

  "submissionsCount:asc": new SortOptionsDto("submissionsCount", "asc"),
  "submissionsCount:desc": new SortOptionsDto("submissionsCount", "desc"),

  "createdAt:asc": new SortOptionsDto("createdAt", "asc"),
  "createdAt:desc": new SortOptionsDto("createdAt", "desc"),

  "updatedAt:asc": new SortOptionsDto("updatedAt", "asc"),
  "updatedAt:desc": new SortOptionsDto("updatedAt", "desc"),
} as const;

export interface FormDetailResponseDto {
  readonly id: string;
  readonly name: string;
  readonly slug: string;
  readonly description: string | null;
  readonly language: Language;
  readonly formStatus: FormStatus;
  readonly shuffleVariant: FormShuffleVariant | null;
  readonly thanksMessage: string | null;
  readonly estimatedDuration: string;
  readonly thumbnail: string | null;
  readonly allowsQuestionsPreview: boolean;
  readonly allowsGuestSubmissions: boolean;
  readonly saveSubmissions: boolean;
  readonly authorName: string | null;
  readonly submissionsCount: number;
  readonly questions: QuestionResponseDto[];
  readonly createdAt: string;
  readonly updatedAt: string;
}

export enum Language {
  En = "EN",
  Pl = "PL",
}

export enum FormStatus {
  Draft = "DRAFT",
  Public = "PUBLIC",
  Unpublic = "UNPUBLIC",
  Private = "PRIVATE",
  Closed = "CLOSED",
}

export enum FormShuffleVariant {
  Questions = "QUESTIONS",
  Answers = "ANSWERS",
  All = "ALL",
}

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
