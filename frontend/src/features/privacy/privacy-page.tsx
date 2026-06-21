import { Card } from "@/core/components/ui/card";
import { getFormatter, getMessages, getTranslations } from "next-intl/server";

const LAST_UPDATED_DATE = new Date("2026-06-21");

export const PrivacyPage = async () => {
  const t = await getTranslations("privacyPage");
  const format = await getFormatter();
  const messages = await getMessages();
  const sections = Object.keys(messages.privacyPage.policy)
    .filter((key) => key.startsWith("header"))
    .map((key) => Number(key.replace("header", "")));

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
                {i + 1}. {t(`policy.header${i}`)}
              </h2>
              <p>{t(`policy.content${i}`)}</p>
            </div>
          );
        })}
      </Card>
    </section>
  );
};
