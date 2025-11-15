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
