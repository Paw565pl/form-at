"use client";

import { Button } from "@/core/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useTranslations } from "next-intl";
import { useTheme } from "next-themes";
import { useEffect, useState } from "react";

export const ThemeSwitcher = () => {
  const t = useTranslations("navBar");
  const { resolvedTheme, setTheme } = useTheme();

  // needed to fix theme hydration error
  const [isMounted, setIsMounted] = useState(false);
  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => setIsMounted(true), []);

  if (!isMounted) {
    return (
      <Button size="icon-sm" variant="outline">
        <ICONS.lightMode />
      </Button>
    );
  }

  const isResolvedThemeDark = resolvedTheme === "dark";

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          aria-label={isResolvedThemeDark ? t("lightMode") : t("darkMode")}
          variant="outline"
          size="icon-sm"
          onClick={() =>
            setTheme((previousTheme) =>
              previousTheme === "dark" ? "light" : "dark",
            )
          }
        >
          {isResolvedThemeDark ? <ICONS.lightMode /> : <ICONS.darkMode />}
        </Button>
      </TooltipTrigger>
      <TooltipContent>
        <span>{isResolvedThemeDark ? t("lightMode") : t("darkMode")}</span>
      </TooltipContent>
    </Tooltip>
  );
};
