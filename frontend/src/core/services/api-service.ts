import { clientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import axios, { CreateAxiosDefaults } from "axios";
import { Session } from "next-auth";
import { getSession } from "next-auth/react";

const axiosDefaults: CreateAxiosDefaults = {
  baseURL:
    typeof window === "undefined"
      ? serverEnv.API_BASE_URL
      : clientEnv.NEXT_PUBLIC_API_BASE_URL,
  timeout: 10_000,
  adapter: "fetch",
} as const;

export const apiService = axios.create(axiosDefaults);

export const authenticatedApiService = axios.create(axiosDefaults);

authenticatedApiService.interceptors.request.use(async (config) => {
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
