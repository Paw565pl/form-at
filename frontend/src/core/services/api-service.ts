import { clientEnv } from "@/core/lib/env/client-env";
import { serverEnv } from "@/core/lib/env/server-env";
import axios from "axios";

const baseUrl =
  typeof window === "undefined"
    ? serverEnv.API_BASE_URL
    : clientEnv.NEXT_PUBLIC_API_BASE_URL;

export const apiService = axios.create({
  baseURL: baseUrl,
  timeout: 10_000,
  adapter: "fetch",
});
