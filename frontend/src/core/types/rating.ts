export interface FormRatingResponseDto {
  readonly id: string;
  readonly value: number;
  readonly createdAt: string;
  readonly updatedAt: string;
}

export interface FormRatingRequestDto {
  readonly value: number;
}
