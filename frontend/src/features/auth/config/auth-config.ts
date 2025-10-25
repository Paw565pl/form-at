import { serverEnv } from "@/core/lib/env/server-env";
import { RefreshedTokensResponseDto } from "@/features/auth/types/refreshed-tokens-response-dto";
import { isRole } from "@/features/auth/types/role";
import { Tokens } from "@/features/auth/types/tokens";
import { User } from "@/features/auth/types/user";
import axios from "axios";
import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

export const { handlers, signIn, signOut, auth } = NextAuth({
  providers: [
    Keycloak({
      clientId: serverEnv.AUTH_KEYCLOAK_ID,
      clientSecret: serverEnv.AUTH_KEYCLOAK_SECRET,
      issuer: serverEnv.AUTH_KEYCLOAK_ISSUER,
      authorization: serverEnv.AUTH_KEYCLOAK_AUTH_URL,
      token: serverEnv.AUTH_KEYCLOAK_TOKEN_URL,
    }),
  ],
  callbacks: {
    jwt: async ({ token, account, profile }) => {
      // First-time login, save the `access_token`, its expiry and the `refresh_token`
      if (account && profile) {
        const roles = profile.realm_access.roles.filter(isRole);
        const user: User = {
          id: profile.sub as string,
          name: profile.preferred_username as string,
          email: profile.email as string,
          image: token.picture,
          roles,
        };

        const tokens: Tokens = {
          accessToken: account.access_token as string,
          accessTokenExpiresIn: account.expires_in as number,
          accessTokenExpiresAt: account.expires_at as number,
          refreshToken: account.refresh_token as string,
          refreshTokenExpiresIn: account.refresh_expires_in as number,
          idToken: account.id_token as string,
        };

        return {
          user,
          tokens,
        };
      }
      // Subsequent logins, but the `access_token` is still valid with 10s buffer
      else if (Date.now() < (token.tokens.accessTokenExpiresAt - 10) * 1000) {
        return token;
      }
      // Subsequent logins, but the `access_token` has expired, try to refresh it
      else {
        try {
          const { data: refreshedTokensResponse } =
            await axios.post<RefreshedTokensResponseDto>(
              serverEnv.AUTH_KEYCLOAK_TOKEN_URL,
              {
                grant_type: "refresh_token",
                refresh_token: token.tokens.refreshToken,
                client_id: serverEnv.AUTH_KEYCLOAK_ID,
                client_secret: serverEnv.AUTH_KEYCLOAK_SECRET,
              },
              {
                headers: {
                  "Content-Type": "application/x-www-form-urlencoded",
                },
              },
            );

          const newTokens: Tokens = {
            accessToken: refreshedTokensResponse.access_token,
            accessTokenExpiresIn: refreshedTokensResponse.expires_in,
            accessTokenExpiresAt: Math.floor(
              Date.now() / 1000 + refreshedTokensResponse.expires_in,
            ),
            refreshToken: refreshedTokensResponse.refresh_token,
            refreshTokenExpiresIn: refreshedTokensResponse.refresh_expires_in,
            idToken: refreshedTokensResponse.id_token,
          };

          return {
            user: token.user,
            tokens: newTokens,
          };
        } catch {
          // Refresh token expired
          return null;
        }
      }
    },
    session: ({ session, token }) => {
      return { expires: session.expires, ...token };
    },
  },
  events: {
    signOut: (message) => {
      if (!("token" in message) || !message.token) return;
      const { tokens } = message.token;

      axios.get(serverEnv.AUTH_KEYCLOAK_LOGOUT_URL, {
        params: {
          id_token_hint: tokens.idToken,
        },
      });
    },
  },
});
