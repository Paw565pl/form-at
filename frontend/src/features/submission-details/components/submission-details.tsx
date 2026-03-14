"use client";

import { Button } from "@/core/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { DeleteSubmissionAlertDialog } from "@/features/submission-details/components/delete-submission-alert-dialog";
import { useFetchSubmissionDetails } from "@/features/submission-details/hooks/use-fetch-submission-details";
import { SubmissionDetailsLoading } from "@/features/submission-details/submission-details-loading";
import { AnswerFeedback } from "@/features/submission/components/answer-feedback";
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
    return <SubmissionDetailsLoading />;

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

        <DeleteSubmissionAlertDialog
          formIdOrSlug={formIdOrSlug}
          submissionId={submissionId}
        />
      </header>

      <AnswerFeedback
        formQuestions={formData.questions}
        answers={submissionData.answers}
      />
    </section>
  );
};
