"use client";

import Link from "next/link";

import { AuthButton } from "@/core/components/auth-button/auth-button";
import { Logo } from "@/core/components/logo/logo";
import { Button } from "@/core/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/core/components/ui/dropdown-menu";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useSession } from "next-auth/react";
import { useLocale, useTranslations } from "next-intl";
import { useTheme } from "next-themes";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export const NavBar = () => {
  const { theme, setTheme } = useTheme();
  const t = useTranslations("navBar");
  const { data: session } = useSession();

  const [mounted, setMounted] = useState(false); // needed for theme checks
  useEffect(() => setMounted(true), []);

  // temporary
  const locale = useLocale();
  const router = useRouter();
  async function SwitchLocale(locale: string) {
    document.cookie = `locale=${locale}`;
    router.refresh();
  }

  const langs = [
    { code: "en", name: "English" },
    { code: "pl", name: "Polski" },
  ];

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
              {/* {t("profile")} */}
              {session?.user?.name}
            </Link>
          </Button>
        )}
        <AuthButton />

        {mounted && (
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                variant="outline"
                size="icon-sm"
                onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
              >
                {theme === "dark" ? <ICONS.lightMode /> : <ICONS.darkMode />}
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <span>{theme === "dark" ? t("lightMode") : t("darkMode")}</span>
            </TooltipContent>
          </Tooltip>
        )}

        <DropdownMenu>
          <Tooltip>
            <TooltipTrigger asChild>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="icon-sm">
                  <ICONS.lang />
                </Button>
              </DropdownMenuTrigger>
            </TooltipTrigger>
            <TooltipContent>
              <span>{t("changeLang")}</span>
            </TooltipContent>
          </Tooltip>

          <DropdownMenuContent align="end">
            {langs.map((lang) => (
              <DropdownMenuItem
                key={lang.code}
                onClick={() => SwitchLocale(lang.code)}
                className="justify-between"
              >
                {lang.name}
                {locale === lang.code && <ICONS.check />}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </nav>
  );
};
