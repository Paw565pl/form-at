export interface ErrorResponseDto {
  readonly timestamp: string;
  readonly status: number;
  readonly error: string;
  readonly message: string;
  readonly errors?: Readonly<Record<string, string[]>>;
}
