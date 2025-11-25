"use client";

import { Button } from "@/core/components/ui/button";
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
    <Button
      aria-label={t("ariaLabel")}
      title={t("ariaLabel")}
      className={cn(
        "fixed right-2.5 bottom-2.5 transition-opacity sm:right-5 sm:bottom-5",
        isVisible ? "opacity-100" : "opacity-0",
      )}
      onClick={scrollToTop}
    >
      <ICONS.scrollToTop />
    </Button>
  );
};
