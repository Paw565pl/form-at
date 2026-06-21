import { createEnv } from "@t3-oss/env-nextjs";
import * as z from "zod";

export const serverEnv = createEnv({
  server: {
    API_BASE_URL: z.url().trim().min(1),
    AUTH_SECRET: z
      .string()
      .trim()
      .min(32, "Auth secret must be at least 32 characters long."),
    AUTH_TRUST_HOST: z.stringbool(),
    AUTH_URL: z.url().trim(),
    AUTH_KEYCLOAK_ID: z.string().trim().min(1),
    AUTH_KEYCLOAK_SECRET: z.string().trim().min(1),
    AUTH_KEYCLOAK_ISSUER: z.url().trim().min(1),
    AUTH_KEYCLOAK_AUTH_URL: z.url().trim().min(1),
    AUTH_KEYCLOAK_TOKEN_URL: z.url().trim().min(1),
    AUTH_KEYCLOAK_LOGOUT_URL: z.url().trim().min(1),
    S3_URL: z.url().trim().min(1),
    OTEL_EXPORTER_OTLP_ENDPOINT: z.url().trim().min(1).optional(),
    OTEL_EXPORTER_OTLP_PROTOCOL: z.string().trim().min(1).optional(),
    OTEL_EXPORTER_OTLP_HEADERS: z.string().trim().min(1).optional(),
  },
  experimental__runtimeEnv: process.env,
  skipValidation: process.env.CI ? true : false,
});
