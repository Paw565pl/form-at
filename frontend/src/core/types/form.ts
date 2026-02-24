import {
  QuestionRequest,
  QuestionRequestDto,
  QuestionResponseDto,
} from "@/core/types/question";

export interface FormListResponseDto {
  readonly id: string;
  readonly name: string;
  readonly slug: string;
  readonly description: string | null;
  readonly language: string;
  readonly status: FormStatus;
  readonly estimatedDuration: FormEstimatedDuration;
  readonly thumbnail: string | null;
  readonly allowsQuestionsPreview: boolean;
  readonly allowsGuestSubmissions: boolean;
  readonly saveSubmissions: boolean;
  readonly showAnswersFeedback: boolean;
  readonly authorName: string | null;
  readonly submissionsCount: number;
  readonly questionsCount: number;
  readonly ratingsCount: number;
  readonly ratingAvg: number | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface FormFilterOptionsDto {
  searchQuery?: string | null;
  language?: Language | null;
  minEstimatedDuration?: string | null;
  maxEstimatedDuration?: string | null;
  allowsGuestSubmissions?: boolean | null;
  authorId?: string | null;
}

export const formSortOptions = [
  "estimatedDuration,asc",
  "estimatedDuration,desc",

  "questionsCount,asc",
  "questionsCount,desc",

  "submissionsCount,asc",
  "submissionsCount,desc",

  "createdAt,asc",
  "createdAt,desc",

  "updatedAt,asc",
  "updatedAt,desc",
] as const;

export type FormSortOption = (typeof formSortOptions)[number];

export interface FormDetailResponseDto {
  readonly id: string;
  readonly name: string;
  readonly slug: string;
  readonly description: string | null;
  readonly language: Language;
  readonly status: FormStatus;
  readonly shuffleVariant: FormShuffleVariant | null;
  readonly thanksMessage: string | null;
  readonly estimatedDuration: FormEstimatedDuration;
  readonly thumbnail: string | null;
  readonly allowsQuestionsPreview: boolean;
  readonly allowsGuestSubmissions: boolean;
  readonly saveSubmissions: boolean;
  readonly showAnswersFeedback: boolean;
  readonly authorName: string | null;
  readonly submissionsCount: number;
  readonly questions: QuestionResponseDto[];
  readonly ratingsCount: number;
  readonly ratingAvg: number | null;
  readonly userRating: number | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export enum Language {
  En = "EN",
  Pl = "PL",
}

export enum FormStatus {
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

export enum FormEstimatedDuration {
  PT1M = "PT1M",
  PT5M = "PT5M",
  PT10M = "PT10M",
  PT15M = "PT15M",
  PT30M = "PT30M",
  PT1H = "PT1H",
  PT2H = "PT2H",
}

export interface FormRequestDto {
  name: string;
  description: string | null;
  language: Language;
  status: FormStatus;
  password: string | null;
  shuffleVariant: FormShuffleVariant | null;
  thanksMessage: string | null;
  estimatedDuration: FormEstimatedDuration;
  thumbnailKey: string | null;
  allowsQuestionsPreview: boolean;
  allowsGuestSubmissions: boolean;
  saveSubmissions: boolean;
  // showAnswersFeedback: boolean; TODO: uncomment when added to form and schema
  questions: QuestionRequestDto[];
}

export interface FormRequest extends Omit<
  FormRequestDto,
  "thumbnailKey" | "questions"
> {
  // file is new file selected by user
  // string is existing url
  thumbnail: File | string | null;
  questions: QuestionRequest[];
}
