import { Badge } from "@/core/components/ui/badge";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { FormResponseDto, FormStatus } from "@/core/types/form";
import { formatDuration } from "@/core/utils/formatDuration";
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
          <ICONS.questionsCount />
          <h2>
            {t("questionsCount", { count: form.questions.length.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <ICONS.submissionsCount />
          <h2>
            {t("submissionsCount", { count: form.submissionsCount.toString() })}
          </h2>
        </div>
        <div className="flex items-center gap-1">
          <ICONS.estimatedDuration />
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
          {form.status == FormStatus.Unpublic ? null : (
            <span className="flex-none">
              <Tooltip>
                <TooltipTrigger asChild>
                  <ICONS.allowGuestSubmissions />
                </TooltipTrigger>
                <TooltipContent side="bottom">
                  <p>{t("unpublicForm")}</p>
                </TooltipContent>
              </Tooltip>
            </span>
          )}
        </div>
        <div className="flex flex-none items-center">
          {/* TODO take submission date from user's submission data */}
          <Badge>{t("formFinished", { finishedAt: "20.12.2025" })}</Badge>
        </div>
      </header>

      {form.description && <p className="pb-6">{form.description}</p>}
      <p className="w-full pb-4 text-right">
        {format.dateTime(form.updatedAt, "long")}
      </p>
    </main>
  );
};
