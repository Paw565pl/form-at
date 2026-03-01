"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Field, FieldLabel } from "@/core/components/ui/field";
import { Progress } from "@/core/components/ui/progress";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { FormStatisticsLoading } from "@/features/submission-statistics/form-statistics-loading";
import { useFetchFormStatistics } from "@/features/submission-statistics/hooks/use-fetch-form-statistics";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { notFound, useRouter } from "next/navigation";

interface StatisticsProps {
  readonly formIdOrSlug: string;
}

export const Statistics = ({ formIdOrSlug }: StatisticsProps) => {
  const t = useTranslations("statisticsPage");
  const gt = useTranslations("global");
  const router = useRouter();

  const {
    data: formData,
    isLoading: isLoadingFormDetails,
    error: formError,
  } = useFetchFormDetails(formIdOrSlug);
  const {
    data: formStatistics,
    isLoading: isLoadingFormStatistics,
    error: formStatisticsError,
  } = useFetchFormStatistics(formIdOrSlug);

  if (formError) {
    if (formError.status === HttpStatusCode.NotFound) return notFound();
    else throw formError;
  } else if (formStatisticsError) {
    if (
      formStatisticsError.status === HttpStatusCode.NotFound ||
      formStatisticsError.status === HttpStatusCode.Conflict
    )
      return notFound();
    else throw formStatisticsError;
  }

  if (
    !formData ||
    !formStatistics ||
    isLoadingFormDetails ||
    isLoadingFormStatistics
  )
    return <FormStatisticsLoading />;

  return (
    <section
      id="statistics"
      className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center">
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                aria-label={t("back")}
                size="icon-sm"
                onClick={() => router.push(`/forms/${formIdOrSlug}`)}
              >
                <ICONS.back />
              </Button>
            </TooltipTrigger>
            <TooltipContent side="bottom">
              <span>{t("back")}</span>
            </TooltipContent>
          </Tooltip>
          <h1 className="ml-4 text-xl font-bold">
            {t("title", { formName: formData.name })}
            <span className="text-muted-foreground block text-sm font-normal">
              {t("noOpenQuestions")}
            </span>
          </h1>
        </div>

        <Button size="sm" asChild className="ml-3">
          <Link href={`/forms/${formIdOrSlug}/submissions`}>
            <ICONS.submissions />
            {t("viewSubmissions")}
          </Link>
        </Button>
      </header>

      {formData.submissionsCount === 0 && (
        <p className="text-center">{t("noSubmissions")}</p>
      )}

      {formData.submissionsCount > 0 && (
        <div className="flex flex-col gap-4">
          {formData.questions
            .filter((question) => question.type !== "OPEN")
            .map((question, index) => {
              const questionStatistics = formStatistics.find(
                (qs) => qs.questionId === question.id,
              );
              return (
                <Card key={question.id} className="gap-2 p-4">
                  <header className="flex flex-col gap-4">
                    <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
                      <div className="flex gap-1">
                        <span className="text-muted-foreground">
                          {index + 1}.
                        </span>
                        <h2 className="font-medium">
                          {question.content}{" "}
                          {question.isRequired && (
                            <span className="text-muted-foreground">*</span>
                          )}
                        </h2>
                      </div>
                      <span className="text-muted-foreground ml-3 text-sm">
                        {gt(`questionTypes.${question.type}`)}
                      </span>
                    </div>
                  </header>
                  <div className="flex flex-col gap-2">
                    {question.answers.map((answer) => {
                      const answerCount =
                        questionStatistics?.submissionStatistics.find(
                          (as) => as.answerId === answer.id,
                        )?.totalCount || 0;
                      const percentage = Math.round(
                        (answerCount / formData.submissionsCount) * 100,
                      );
                      return (
                        <Field className="w-full max-w-sm" key={answer.id}>
                          <FieldLabel htmlFor={`${answer.id}-percentage`}>
                            <span>{answer.content}</span>
                            <span className="ml-auto">
                              {percentage}% (
                              {t("answerCount", { count: answerCount })})
                            </span>
                          </FieldLabel>
                          <Progress
                            value={percentage}
                            id={`${answer.id}-percentage`}
                            aria-label={`${answer.content} - ${percentage}%`}
                          />
                        </Field>
                      );
                    })}
                  </div>
                </Card>
              );
            })}
        </div>
      )}
    </section>
  );
};
