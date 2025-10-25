import { createEnv } from "@t3-oss/env-nextjs";
import { z } from "zod";

export const serverEnv = createEnv({
  server: {
    API_BASE_URL: z.url().trim(),
    AUTH_SECRET: z
      .string()
      .trim()
      .min(32, "Auth secret must be at least 32 characters long."),
    AUTH_TRUST_HOST: z.coerce.boolean(),
    AUTH_URL: z.url().trim(),
    AUTH_KEYCLOAK_ID: z.string().trim().min(1),
    AUTH_KEYCLOAK_SECRET: z.string().trim().min(1),
    AUTH_KEYCLOAK_ISSUER: z.url().trim().min(1),
    AUTH_KEYCLOAK_AUTH_URL: z.url().trim().min(1),
    AUTH_KEYCLOAK_TOKEN_URL: z.url().trim().min(1),
    AUTH_KEYCLOAK_LOGOUT_URL: z.url().trim().min(1),
  },
  experimental__runtimeEnv: process.env,
  skipValidation: process.env.CI ? true : false,
});
