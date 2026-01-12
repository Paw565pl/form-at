import { Button } from "@/core/components/ui/button";
import { ICONS } from "@/core/config/icons";
import { useTranslations } from "next-intl";
import Link from "next/link";

export const NotFoundPage = () => {
  const t = useTranslations("notFoundPage");

  return (
    <section className="flex w-full flex-1 flex-col items-center justify-center gap-4 px-4 text-center">
      <div className="bg-muted flex size-20 items-center justify-center rounded-full">
        <ICONS.notFound className="text-muted-foreground size-10" />
      </div>

      <div className="space-y-2">
        <h1 className="text-2xl font-bold tracking-tighter">{t("header")}</h1>
        <p className="text-muted-foreground max-w-lg text-balance">
          {t("message")}
        </p>
      </div>

      <Button asChild variant="default" size="lg" className="mt-4">
        <Link href="/">
          <ICONS.home />
          {t("returnButton")}
        </Link>
      </Button>
    </section>
  );
};
