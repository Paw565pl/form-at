"use client";

import { Button } from "@/core/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";

interface ScrollToTopButtonProps {
  readonly showAfter?: number;
}

export const ScrollToTopButton = ({
  showAfter = 500,
}: ScrollToTopButtonProps) => {
  const t = useTranslations("components.scrollToTopButton");
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const toggleVisibility = () =>
      window.scrollY > showAfter ? setIsVisible(true) : setIsVisible(false);

    window.addEventListener("scroll", toggleVisibility);
    return () => window.removeEventListener("scroll", toggleVisibility);
  }, [showAfter]);

  const scrollToTop = () =>
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });

  return (
    <Tooltip>
      <TooltipTrigger
        asChild
        className={cn(
          "fixed right-2.5 bottom-2.5 transition-opacity sm:right-5 sm:bottom-5",
          isVisible ? "opacity-100" : "pointer-events-none opacity-0",
        )}
      >
        <Button
          aria-label={t("ariaLabel")}
          onClick={scrollToTop}
          tabIndex={isVisible ? 0 : -1}
          aria-hidden={!isVisible}
        >
          <ICONS.scrollToTop />
        </Button>
      </TooltipTrigger>
      <TooltipContent>{t("ariaLabel")}</TooltipContent>
    </Tooltip>
  );
};
