import { createEnv } from "@t3-oss/env-nextjs";
import { z } from "zod";

export const serverEnv = createEnv({
  server: {
    API_BASE_URL: z.url().trim(),
  },
  experimental__runtimeEnv: process.env,
  skipValidation: process.env.CI ? true : false,
});
