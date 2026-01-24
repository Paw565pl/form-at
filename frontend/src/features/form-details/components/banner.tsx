"use client";

import { Badge } from "@/core/components/ui/badge";
import { Button } from "@/core/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { FormImageWithFallback } from "@/core/form-image/form-image-with-fallback";
import { FormDetailResponseDto } from "@/core/types/form";
import { FormOptions } from "@/features/form-details/components/form-options";
import { useFetchMySubmission } from "@/features/form-details/my-submission/hooks/use-fetch-my-submission";
import { useFormatter, useTranslations } from "next-intl";
import { useRouter } from "next/navigation";

interface BannerProps {
  readonly form: FormDetailResponseDto;
}

export const Banner = ({ form }: BannerProps) => {
  const t = useTranslations("formDetailsPage.banner");
  const format = useFormatter();
  const router = useRouter();

  const { data: mySubmission } = useFetchMySubmission(form.slug);

  return (
    <section className="relative flex h-48 w-full items-end">
      {/* background image */}
      <FormImageWithFallback
        src={form.thumbnail}
        alt={form.name}
        fill
        className="rounded-t-md object-cover"
      />

      {/* go to forms list button */}
      <span className="absolute top-4 left-4">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button
              aria-label={t("back")}
              size="icon-sm"
              onClick={() => router.push("/forms")}
            >
              <ICONS.back />
            </Button>
          </TooltipTrigger>
          <TooltipContent side="bottom">
            <span>{t("back")}</span>
          </TooltipContent>
        </Tooltip>
      </span>

      {mySubmission && (
        <Badge className="absolute top-4 right-4">
          {t("formFinished", {
            finishedAt: format.dateTime(
              new Date(mySubmission.createdAt),
              "long",
            ),
          })}
        </Badge>
      )}

      <div className="absolute right-2 bottom-2 flex flex-col items-center gap-2 md:right-4 md:bottom-4 md:flex-row">
        {!mySubmission && (
          <Button
            onClick={() => router.push(`/forms/${form.slug}/submissions/new`)}
          >
            <ICONS.fillForm />
            {t("fillOutForm")}
          </Button>
        )}

        {/* more options */}
        <FormOptions slug={form.slug} authorName={form.authorName} />
      </div>
    </section>
  );
};
