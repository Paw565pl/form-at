import { serverEnv } from "@/core/lib/env/server-env";
import { isRole } from "@/features/auth/types/role";
import { Tokens } from "@/features/auth/types/tokens";
import { User } from "@/features/auth/types/user";
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
    jwt: ({ token, account, profile }) => {
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
          resfrehTokenExpiresIn: account.expires_in as number,
          idToken: account.id_token as string,
        };

        return {
          user,
          tokens,
        };
      }

      return token;
    },
    session: ({ session, token }) => {
      return { expires: session.expires, ...token };
    },
  },
});
