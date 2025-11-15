export interface FormDetailResponseDto {
  readonly id: string;
  readonly name: string;
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

export interface QuestionResponseDto {
  readonly id: string;
  readonly content: string;
  readonly type: QuestionType;
  readonly image: string | null;
  readonly isRequired: boolean;
  readonly answers: AnswerResponseDto[];
}

export enum QuestionType {
  SingleChoice = "SINGLE_CHOICE",
  MultipleChoice = "MULTIPLE_CHOICE",
  Open = "OPEN",
}

export interface AnswerResponseDto {
  readonly id: string;
  readonly content: string;
  readonly isCorrect: boolean;
}
