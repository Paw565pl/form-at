"use client";

import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import { Checkbox } from "@/core/components/ui/checkbox";
import { Label } from "@/core/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/core/components/ui/radio-group";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { cn } from "@/core/lib/cn";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { DeleteSubmission } from "@/features/submission-details/components/delete-submission";
import { useFetchSubmissionDetails } from "@/features/submission-details/hooks/use-fetch-submission-details";
import { HttpStatusCode } from "axios";
import { useTranslations } from "next-intl";
import Link from "next/link";
import { notFound } from "next/navigation";

interface SubmissionDetailsProps {
  readonly formIdOrSlug: string;
  readonly submissionId: string;
}

export const SubmissionDetails = ({
  formIdOrSlug,
  submissionId,
}: SubmissionDetailsProps) => {
  const t = useTranslations("submissionDetailsPage");
  const gt = useTranslations("global");

  const {
    data: formData,
    isLoading: isFormLoading,
    error: formError,
  } = useFetchFormDetails(formIdOrSlug);
  const { data: submissionData, isLoading: isSubmissionLoading } =
    useFetchSubmissionDetails(formIdOrSlug, submissionId);

  if (formError) {
    if (
      formError.status === HttpStatusCode.NotFound ||
      formError.status === HttpStatusCode.Conflict
    )
      return notFound();
    else throw formError;
  }

  if (!formData || !submissionData || isFormLoading || isSubmissionLoading)
    return <p>{t("loading")}</p>;

  return (
    <section
      id="form-create"
      className="flex w-full flex-col gap-4 px-5 py-10 lg:px-30"
    >
      <header className="flex gap-2">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button asChild size="icon-sm" aria-label={t("backToSubmissions")}>
              <Link href={`/forms/${formIdOrSlug}/submissions`}>
                <ICONS.back />
              </Link>
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <span>{t("backToSubmissions")}</span>
          </TooltipContent>
        </Tooltip>

        <h2 className="text-xl font-bold">
          {submissionData.authorName
            ? t("submissionBy", { authorName: submissionData.authorName })
            : t("submissionByUnknown")}
        </h2>

        <DeleteSubmission
          formIdOrSlug={formIdOrSlug}
          submissionId={submissionId}
        />
      </header>

      <div className="flex flex-col gap-4">
        {formData.questions.map((question, index) => {
          const openAnswer = submissionData.answers.find(
            (a) => a.questionId === question.id,
          )?.openAnswer;
          const selectedOptions =
            submissionData.answers.find((a) => a.questionId === question.id)
              ?.chosenAnswerIds || [];

          return (
            <Card key={question.id} className="gap-2 p-4">
              <header className="flex flex-col gap-4">
                <div className="flex flex-col flex-wrap gap-1 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex gap-1">
                    <span className="text-muted-foreground">{index + 1}.</span>
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

              {question.type === "OPEN" &&
                (openAnswer ? (
                  <p className="mx-3 text-sm">{openAnswer}</p>
                ) : (
                  <p className="text-muted-foreground mx-3 text-sm">
                    {t("emptyOpenAnswer")}
                  </p>
                ))}

              {question.type === "SINGLE_CHOICE" && (
                <RadioGroup disabled value={selectedOptions[0]}>
                  {question.answers.map((answer) => (
                    <div key={answer.id} className="flex items-center gap-2">
                      <RadioGroupItem
                        value={answer.id}
                        id={answer.id}
                        className="cursor-default! opacity-100!"
                      />
                      <Label htmlFor={answer.id} className="font-normal">
                        {answer.content}
                      </Label>
                      {answer.isCorrect ? (
                        <ICONS.check
                          className={cn(
                            "text-green-400",
                            selectedOptions[0] !== answer.id && "opacity-30",
                          )}
                        />
                      ) : (
                        <ICONS.close
                          className={cn(
                            "text-destructive",
                            selectedOptions[0] !== answer.id && "opacity-30",
                          )}
                        />
                      )}
                    </div>
                  ))}
                </RadioGroup>
              )}

              {question.type === "MULTIPLE_CHOICE" && (
                <div className="flex flex-col gap-2">
                  {question.answers.map((answer) => (
                    <div key={answer.id} className="flex items-center gap-2">
                      <Checkbox
                        checked={selectedOptions.includes(answer.id)}
                        id={answer.id}
                        disabled
                        className="cursor-default! opacity-100!"
                      />
                      <Label
                        htmlFor={answer.id}
                        className="cursor-default! font-normal opacity-100!"
                      >
                        {answer.content}
                      </Label>
                      {answer.isCorrect ? (
                        <ICONS.check
                          className={cn(
                            "text-green-400",
                            !selectedOptions.includes(answer.id) &&
                              "opacity-30",
                          )}
                        />
                      ) : (
                        <ICONS.close
                          className={cn(
                            "text-destructive",
                            !selectedOptions.includes(answer.id) &&
                              "opacity-30",
                          )}
                        />
                      )}
                    </div>
                  ))}
                </div>
              )}
            </Card>
          );
        })}
      </div>
    </section>
  );
};
