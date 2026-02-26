export interface ErrorResponseDto {
  readonly detail: string;
  readonly instance: string;
  readonly status: number;
  readonly title: string;
  readonly code: string;
  readonly errors?: Readonly<Record<string, string[]>>;
}
