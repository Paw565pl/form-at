"use client";

import { Badge, BadgeVariant } from "@/core/components/ui/badge";
import { useFormatter } from "next-intl";

interface HistoryItemProps {
  content: string;
  formName: string;
  date: Date;
  badgeVariant?: BadgeVariant;
}

export const HistoryItem = ({
  content,
  formName,
  date,
  badgeVariant,
}: HistoryItemProps) => {
  const format = useFormatter();

  return (
    <section className="bg-card flex flex-col items-start justify-between gap-1 rounded-md p-2 shadow-sm md:flex-row md:items-center">
      <div className="flex flex-col gap-2 md:flex-row">
        {content}
        <Badge variant={badgeVariant} className="text-white">
          {formName}
        </Badge>
      </div>
      <Badge className="flex-none self-end md:self-start" variant={"outline"}>
        {format.dateTime(new Date(date), "long")}
      </Badge>
    </section>
  );
};
