"use client";

import { Badge } from "@/core/components/ui/badge";
import { UserHistoryItemDto } from "@/core/types/user-profile";
import { useFormatter, useTranslations } from "next-intl";

interface HistoryProps {
  history: UserHistoryItemDto[];
}

export const History = ({ history }: HistoryProps) => {
  const format = useFormatter();
  const t = useTranslations("userProfilePage");

  if (history.length === 0) {
    return <p className="self-center">{t("noHistory")}</p>;
  }

  return (
    <>
      {history.map((item) => (
        <section
          key={item.id}
          className="bg-card flex flex-col items-start justify-between gap-1 rounded-md p-2 shadow-sm md:flex-row md:items-center"
        >
          <div className="flex flex-row gap-2">
            {item.content}
            <Badge
              variant={item.badgeVariant}
              className="self-center text-white"
            >
              {item.formName}
            </Badge>
          </div>
          <Badge
            className="flex-none self-end md:self-start"
            variant={"outline"}
          >
            {format.dateTime(new Date(item.date), "long")}
          </Badge>
        </section>
      ))}
    </>
  );
};
