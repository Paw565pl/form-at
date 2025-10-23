export interface RefreshTokenResponseDto {
  readonly access_token: string;
  readonly expires_in: number;
  readonly refresh_token: string;
  readonly refresh_expires_in: number;
  readonly id_token: string;
}
