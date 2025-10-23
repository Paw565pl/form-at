import { serverEnv } from "@/core/lib/env/server-env";
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
});
