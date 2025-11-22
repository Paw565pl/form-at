"use client";

import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { signIn, signOut, useSession } from "next-auth/react";
import { useTranslations } from "next-intl";

export const AuthButton = () => {
  const t = useTranslations("navBar.auth");
  const { data: session } = useSession();

  if (!session)
    return (
      <Button size="sm" onClick={() => signIn("keycloak")}>
        <ICONS.login />
        {t("signIn")}
      </Button>
    );

  return (
    <Button size="sm" onClick={() => signOut({ redirectTo: "/" })}>
      <ICONS.logout />
      {t("signOut")}
    </Button>
  );
};
