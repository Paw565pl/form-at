export interface UserProfileResponseDto {
  readonly id: string;
  readonly username: string;
  readonly statistics: UserStatistics;
}

export interface UserStatistics {
  readonly formsCount: number;
  readonly submissionsCount: number;
  readonly commentsCount: number;
}
