import { Card } from "@/core/components/ui/card";
import { getFormatter, getTranslations } from "next-intl/server";

const LAST_UPDATED_DATE = new Date("2026-06-21");

export const PrivacyPage = async () => {
  const t = await getTranslations("privacyPage");
  const format = await getFormatter();
  const sections = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] as const;

  return (
    <section id="privacy" className="px-5 py-10 lg:px-30">
      <Card className="flex w-full flex-col justify-between gap-4 p-4">
        <div className="flex justify-between gap-4">
          <h1 className="text-2xl">{t("title")}</h1>
          <span className="text-muted-foreground">
            {t("lastUpdated")}: {format.dateTime(LAST_UPDATED_DATE, "long")}
          </span>
        </div>

        {sections.map((i) => {
          return (
            <div key={i} className="flex flex-col gap-1">
              <h2 className="text-muted-foreground">
                {i}. {t(`policy.header${i}`)}
              </h2>
              <p>{t(`policy.content${i}`)}</p>
            </div>
          );
        })}
      </Card>
    </section>
  );
};
