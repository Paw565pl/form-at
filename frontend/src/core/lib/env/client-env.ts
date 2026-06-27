import { createEnv } from "@t3-oss/env-nextjs";
import { env } from "next-runtime-env";
import * as z from "zod";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const clientEnv = createEnv({
  client: {
    NEXT_PUBLIC_API_BASE_URL: z.url().trim().min(1),
    NEXT_PUBLIC_S3_URL: z.url().trim().min(1),
    NEXT_PUBLIC_APP_URL: z.url().trim().min(1),
  },
  runtimeEnv: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL,
    NEXT_PUBLIC_S3_URL: process.env.NEXT_PUBLIC_S3_URL,
    NEXT_PUBLIC_APP_URL: process.env.NEXT_PUBLIC_APP_URL,
  },
  skipValidation: true,
  emptyStringAsUndefined: true,
});

type ClientEnvKey = keyof typeof clientEnv;

/**
 * Throws error if value is not defined or empty and defaultValue is not provided
 */
export const getClientEnv = (key: ClientEnvKey, defaultValue?: string) => {
  const value = env(key);
  if (value) return value;

  if (defaultValue) return defaultValue;
  throw new Error(`Missing client env: ${key}`);
};
