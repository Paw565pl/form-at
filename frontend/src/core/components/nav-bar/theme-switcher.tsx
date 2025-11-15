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
  const { theme, setTheme } = useTheme();

  const [mounted, setMounted] = useState(false); // needed for theme checks
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setMounted(true);
  }, []);

  if (!mounted) return null;

  return (
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
  );
};
