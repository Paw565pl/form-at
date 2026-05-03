import { Button } from "@/core/components/ui/button";
import { getTranslations } from "next-intl/server";
import Link from "next/link";

export const Footer = async () => {
  const t = await getTranslations("footer");
  const currentYear = new Date().getFullYear();
  const repoUrl = "https://github.com/Paw565pl/form-at";

  return (
    <footer className="bg-background flex w-full flex-col items-center justify-center gap-2 p-2 sm:flex-row">
      <span className="text-muted-foreground px-3 text-sm">
        &copy; {currentYear} formAT
      </span>

      <Button size="sm" asChild variant="link">
        <Link href="/privacy">{t("privacy")}</Link>
      </Button>

      <Button size="sm" asChild variant="link">
        <Link href={repoUrl} target="_blank" rel="noopener noreferrer">
          {t("GitHub")}
        </Link>
      </Button>
    </footer>
  );
};
