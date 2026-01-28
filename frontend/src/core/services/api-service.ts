import { getClientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import axios from "axios";
import { Session } from "next-auth";
import { getSession } from "next-auth/react";

export const apiService = axios.create({
  baseURL:
    typeof window === "undefined"
      ? serverEnv.API_BASE_URL
      : getClientEnv("NEXT_PUBLIC_API_BASE_URL"),
  timeout: 10_000,
});

apiService.interceptors.request.use(async (config) => {
  let session: Session | null = null;

  if (typeof window === "undefined") {
    session = await (await import("@/features/auth/config/auth-config")).auth();
  } else {
    session = await getSession();
  }

  if (session)
    config.headers.Authorization = `Bearer ${session.tokens.accessToken}`;

  return config;
});
