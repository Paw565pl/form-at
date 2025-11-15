"use client";

import { AuthButton } from "@/core/components/auth-button/auth-button";
import { Logo } from "@/core/components/logo/logo";
import { LanguageSwitcher } from "@/core/components/nav-bar/language-switcher";
import { ThemeSwitcher } from "@/core/components/nav-bar/theme-switcher";
import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import Link from "next/link";

export const NavBar = () => {
  const t = useTranslations("navBar");
  const { data: session } = useSession();

  return (
    <nav className="flex w-full justify-between p-2">
      <div className="links flex items-center gap-2">
        <div className="text-primary mr-2 flex items-center text-lg font-semibold">
          <Logo />
          <h2>formAT</h2>
        </div>

        <Button size="sm" asChild variant="outline">
          <Link href="/">
            <ICONS.home />
            {t("home")}
          </Link>
        </Button>
        <Button size="sm" asChild variant="outline">
          <Link href="/forms">
            <ICONS.form />
            {t("forms")}
          </Link>
        </Button>
        {session && (
          <Button size="sm" asChild>
            <Link href="/forms/new">
              <ICONS.formNew />
              {t("createForm")}
            </Link>
          </Button>
        )}
      </div>

      <div className="actions flex items-center gap-2">
        {session && (
          <Button size="sm" asChild>
            <Link href="/profile">
              <ICONS.user />
              {session?.user?.name}
            </Link>
          </Button>
        )}

        <AuthButton />
        <ThemeSwitcher />
        <LanguageSwitcher />
      </div>
    </nav>
  );
};
