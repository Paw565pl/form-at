import { Badge } from "@/core/components/ui/badge";
import { FormResponseDto } from "@/core/types/form";
import { formatDuration } from "@/core/utils/formatDuration";
import {
  BadgeQuestionMark,
  ClockArrowUp,
  Lock,
  PersonStanding,
} from "lucide-react";
import { useFormatter, useTranslations } from "next-intl";
interface DetailsProps {
  form: FormResponseDto;
}

export const Details = ({ form }: DetailsProps) => {
  const format = useFormatter();
  const t = useTranslations("publicFormView.details");

  return (
    <main className="w-full pt-7">
      {/* form tags */}
      <div className="flex flex-wrap gap-2 md:gap-6">
        <div className="flex items-center gap-1">
          <BadgeQuestionMark />
          <h2>
            {t("questionsCount", { count: form.questions.length.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <PersonStanding />
          <h2>
            {t("submissionsCount", { count: form.submissionsCount.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <ClockArrowUp />
          <h2>
            {t("estimatedTime", {
              time: formatDuration(form.estimatedDuration),
            })}
          </h2>
        </div>
      </div>

      <header className="flex flex-col-reverse items-start justify-between py-2 md:flex-row">
        <div className="flex flex-1 items-center gap-2">
          <h1 className="line-clamp-2 max-w-2xl text-4xl font-extrabold">
            {form.name}
          </h1>
          {form.allowsGuestSubmissions ? null : <Lock className="flex-none" />}
        </div>
        <div className="flex flex-none items-center">
          {/* TODO take submission date from user's submission data */}
          <Badge>{t("formFinished", { finishedAt: "20.12.2025" })}</Badge>
        </div>
      </header>

      {form.description && <p>{form.description}</p>}
      <br />
      <p className="w-full pb-4 text-right">
        {format.dateTime(form.updatedAt, {
          year: "numeric",
          month: "numeric",
          day: "numeric",
        })}
      </p>
    </main>
  );
};
