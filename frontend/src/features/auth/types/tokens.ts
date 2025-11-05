export interface Tokens {
  readonly accessToken: string;
  readonly accessTokenExpiresIn: number;
  readonly accessTokenExpiresAt: number;
  readonly refreshToken: string;
  readonly refreshTokenExpiresIn: number;
  readonly idToken: string;
}
