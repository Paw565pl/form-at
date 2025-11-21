"use client";

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
import { ValidLocale } from "@/core/lib/i18n/request";
import { switchLocale } from "@/core/lib/i18n/switch-locale";
import { useLocale, useTranslations } from "next-intl";

const langs: { code: ValidLocale; name: string }[] = [
  { code: "en", name: "English" },
  { code: "pl", name: "Polski" },
] as const;

export const LanguageSwitcher = () => {
  const t = useTranslations("navBar");
  const locale = useLocale();

  return (
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
            onClick={() => switchLocale(lang.code)}
            className="justify-between"
          >
            {lang.name}
            {locale === lang.code && <ICONS.check />}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
