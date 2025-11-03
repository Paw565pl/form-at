import { Badge } from "@/core/components/ui/badge";
import {
  BadgeQuestionMark,
  ClockArrowUp,
  Lock,
  PersonStanding,
} from "lucide-react";
import ms from "ms";
import { useFormatter, useTranslations } from "next-intl";
import { FormResponseDto } from "../types/form-response-dto";
interface DetailsProps {
  form: FormResponseDto;
}

export const Details = ({ form }: DetailsProps) => {
  const format = useFormatter();
  const dateTime = new Date(form.updatedAt);
  const t = useTranslations("PublicFormView.Details");
  return (
    <div>
      <div className="flex gap-6 text-gray-500">
        <div className="flex items-center gap-1">
          <BadgeQuestionMark />
          <h2 className="hidden md:block">
            {t("questionsCount", { count: form.questions.length.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <PersonStanding />
          <h2>
            {t("submissionsCount", { count: form.submissionCount.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <ClockArrowUp />
          <h2>
            {t("estimatedTime", {
              time: ms(form.estimatedDuration),
            })}
          </h2>
        </div>
      </div>
      <div className="my-2 flex justify-between">
        <div className="flex items-center gap-2">
          <h1 className="text-4xl font-extrabold">{form.name}</h1>
          <Lock />
        </div>
        <Badge>{t("quizFinished", { finishedAt: "20.12.2025" })}</Badge>
      </div>
      <p>{form.description}</p>
      <p className="w-full text-right text-gray-500">
        {format.dateTime(dateTime, {
          year: "numeric",
          month: "long",
          day: "numeric",
        })}
      </p>
    </div>
  );
};
