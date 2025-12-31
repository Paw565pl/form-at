"use client";

import { AuthButton } from "@/core/components/auth-button/auth-button";
import { Logo } from "@/core/components/logo/logo";
import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useSession } from "next-auth/react";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { useState } from "react";

export const MobileMenu = () => {
  const t = useTranslations("navBar");
  const { data: session } = useSession();
  const [open, setOpen] = useState(false);

  return (
    <nav className="md:hidden">
      <Tooltip>
        <TooltipTrigger asChild>
          <Button
            aria-label={t("openMobileMenu")}
            variant="outline"
            size="icon-sm"
            onClick={() => setOpen(true)}
          >
            <ICONS.menu />
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <span>{t("openMobileMenu")}</span>
        </TooltipContent>
      </Tooltip>

      {open && (
        <Card className="absolute top-0 left-0 z-10 w-full gap-2 rounded-t-none p-2">
          <header className="flex items-center justify-between">
            <div className="text-primary flex items-center text-lg font-semibold">
              <Logo />
              <h2>formAT</h2>
            </div>
            <Button
              variant="ghost"
              size="icon-sm"
              onClick={() => setOpen(false)}
            >
              <ICONS.close />
            </Button>
          </header>

          <div className="flex flex-col gap-2">
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
              <>
                <Button size="sm" asChild>
                  <Link href="/forms/new">
                    <ICONS.formNew />
                    {t("createForm")}
                  </Link>
                </Button>
                <Button size="sm" asChild>
                  <Link href={`/users/${session?.user?.name}`}>
                    <ICONS.user />
                    {session?.user?.name}
                  </Link>
                </Button>
              </>
            )}
            <AuthButton />
          </div>
        </Card>
      )}
    </nav>
  );
};
