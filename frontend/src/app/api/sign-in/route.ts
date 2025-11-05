import { signIn } from "@/features/auth/config/auth-config";
import { NextRequest } from "next/server";

export const GET = ({ url }: NextRequest) => {
  const searchParams = new URL(url).searchParams;
  const redirectTo = searchParams.get("redirectTo") ?? undefined;

  return signIn("keycloak", { redirectTo });
};
