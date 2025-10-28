import { createEnv } from "@t3-oss/env-nextjs";
import { env } from "next-runtime-env";
import { z } from "zod";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const clientEnv = createEnv({
  client: {
    NEXT_PUBLIC_API_BASE_URL: z.url().trim(),
  },
  runtimeEnv: {
    NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL,
  },
  skipValidation: true,
});

type ClientEnvKey = keyof typeof clientEnv;

export const getClientEnv = (key: ClientEnvKey) => env(key);
