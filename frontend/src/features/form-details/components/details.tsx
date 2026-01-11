import { Badge } from "@/core/components/ui/badge";
import { Card } from "@/core/components/ui/card";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { FormDetailResponseDto, FormStatus } from "@/core/types/form";
import { formatDuration } from "@/core/utils/format-duration";
import { useFormatter, useTranslations } from "next-intl";
import { FormRating } from "../rating/hooks/form-rating";

interface DetailsProps {
  readonly form: FormDetailResponseDto;
}

export const Details = ({ form }: DetailsProps) => {
  const format = useFormatter();
  const t = useTranslations("formDetailsPage.details");

  return (
    <Card className="flex w-full flex-col gap-4 rounded-t-none p-4">
      <header className="flex flex-wrap items-center gap-2 md:flex-row">
        {form.status === FormStatus.Private && (
          <Tooltip>
            <TooltipTrigger>
              <ICONS.privateForm />
            </TooltipTrigger>
            <TooltipContent side="bottom">
              <span>{t("privateForm")}</span>
            </TooltipContent>
          </Tooltip>
        )}

        <h1 className="line-clamp-2 max-w-2xl text-2xl">{form.name}</h1>

        {/* TODO take submission date from user's submission data */}
        <Badge className="text-wrap md:ml-auto">
          {t("formFinished", {
            finishedAt: format.dateTime(new Date(), "long"),
          })}
        </Badge>
      </header>

      <FormRating />

      {form.description && <p>{form.description}</p>}

      {/* form tags */}
      <div className="text-muted-foreground flex flex-wrap items-center gap-2 text-sm md:gap-6">
        <div className="flex items-center gap-1">
          <ICONS.questionsCount />
          <h2>{t("questionsCount", { count: form.questions.length })}</h2>
        </div>
        <div className="flex items-center gap-1">
          <ICONS.submissionsCount />
          <h2>{t("submissionsCount", { count: form.submissionsCount })}</h2>
        </div>
        <div className="flex items-center gap-1">
          <ICONS.estimatedDuration />
          <h2>
            {t("estimatedTime", {
              time: formatDuration(form.estimatedDuration),
            })}
          </h2>
        </div>
        <p className="ml-auto">
          {t("createdAt", {
            date: format.dateTime(new Date(form.updatedAt), "long"),
          })}
        </p>
      </div>
    </Card>
  );
};
