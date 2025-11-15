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
import { useLocale, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";

const langs = [
  { code: "en", name: "English" },
  { code: "pl", name: "Polski" },
];

export const LanguageSwitcher = () => {
  const t = useTranslations("navBar");
  const locale = useLocale();
  const router = useRouter();

  const SwitchLocale = (locale: string) => {
    // eslint-disable-next-line react-hooks/immutability
    document.cookie = `locale=${locale}`;
    router.refresh();
  };

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
            onClick={() => SwitchLocale(lang.code)}
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
