"use client";

import { Button } from "@/core/components/ui/button";
import { signIn, signOut, useSession } from "next-auth/react";
import { useTranslations } from "next-intl";

export const AuthButton = () => {
  const t = useTranslations("navbar.auth");
  const { data: session } = useSession();

  if (!session)
    return <Button onClick={() => signIn("keycloak")}>{t("signIn")}</Button>;

  return (
    <Button onClick={() => signOut({ redirectTo: "/" })}>{t("signOut")}</Button>
  );
};
