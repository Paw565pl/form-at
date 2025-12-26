export interface PaginatedResponseDto<T> {
  readonly content: T[];
  readonly page: {
    readonly size: number;
    readonly number: number;
    readonly totalElements: number;
    readonly totalPages: number;
  };
}
