import { Badge } from "@/core/components/ui/badge";
import { FormResponseDto } from "@/features/form-details/types/form-response-dto";
import {
  BadgeQuestionMark,
  ClockArrowUp,
  Lock,
  PersonStanding,
} from "lucide-react";
import ms from "ms";
import { useTranslations } from "next-intl";
interface DetailsProps {
  form: FormResponseDto;
}

export const Details = ({ form }: DetailsProps) => {
  const t = useTranslations("PublicFormView.Details");
  return (
    <div>
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
      <div className="my-2 flex flex-col-reverse items-start justify-between md:flex-row">
        <div className="flex flex-1 items-center gap-2">
          <h1 className="line-clamp-2 max-w-2xl text-4xl font-extrabold">
            {form.name}
          </h1>
          {form.allowsGuestSubmissions ? null : <Lock className="flex-none" />}
        </div>
        <div className="flex flex-none items-center">
          <Badge>{t("quizFinished", { finishedAt: "20.12.2025" })}</Badge>
        </div>
      </div>
      {form.description && <p>{form.description}</p>}
      <p className="w-full text-right">{form.updatedAt}</p>
    </div>
  );
};
