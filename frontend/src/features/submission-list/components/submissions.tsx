"use client";

import { ScrollToTopButton } from "@/core/components/scroll-to-top-button/scroll-to-top-button";
import { Button } from "@/core/components/ui/button";
import { Card } from "@/core/components/ui/card";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/core/components/ui/tooltip";
import { ICONS } from "@/core/config/icons";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { useFetchSubmissionPages } from "@/features/submission-list/hooks/use-fetch-submission-pages";
import {
  SubmissionCardSkeleton,
  SubmissionListLoading,
} from "@/features/submission-list/submission-list-loading";
import { HttpStatusCode } from "axios";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";
import { notFound, useRouter } from "next/navigation";
import InfiniteScroll from "react-infinite-scroll-component";

interface SubmissionsProps {
  readonly formIdOrSlug: string;
}

export const Submissions = ({ formIdOrSlug }: SubmissionsProps) => {
  const t = useTranslations("submissionListPage");
  const format = useFormatter();
  const router = useRouter();

  const {
    data: submissionPages,
    error,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchSubmissionPages(formIdOrSlug);
  const { data: formData } = useFetchFormDetails(formIdOrSlug);

  if (error) {
    if (
      error.status === HttpStatusCode.NotFound ||
      error.status === HttpStatusCode.Conflict
    )
      return notFound();
    else throw error;
  }

  if (!formData) return <SubmissionListLoading />;

  const totalElements = submissionPages?.pages.at(0)?.page.totalElements || 0;

  const dataLength =
    submissionPages?.pages.reduce(
      (acc, curr) => acc + curr.content.length,
      0,
    ) || 0;

  return (
    <section
      id="submissions-list"
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
            {t("title", { count: totalElements, formName: formData.name })}
          </h1>
        </div>

        <Button size="sm" asChild className="ml-3">
          <Link href={`/forms/${formIdOrSlug}/submissions/statistics`}>
            <ICONS.statistics />
            {t("viewStatistics")}
          </Link>
        </Button>
      </header>

      <InfiniteScroll
        dataLength={dataLength}
        next={fetchNextPage}
        hasMore={hasNextPage}
        loader={null}
        className="flex flex-col gap-2"
      >
        {submissionPages?.pages.map((page) =>
          page.content.map((submission) => (
            <Link
              href={`/forms/${formIdOrSlug}/submissions/${submission.id}`}
              key={submission.id}
            >
              <Card
                key={submission.id}
                className="hover:border-primary flex-col-reverse justify-between p-4 transition-all sm:flex-row"
              >
                <p className="font-semibold">
                  {submission.authorName || t("unknownAuthor")}
                </p>
                <span className="text-muted-foreground text-sm">
                  {t("createdAt", {
                    date: format.dateTime(
                      new Date(submission.createdAt),
                      "long",
                    ),
                  })}
                </span>
              </Card>
            </Link>
          )),
        )}
      </InfiniteScroll>

      {submissionPages?.pages.at(0)?.content.length == 0 && (
        <p className="p-4 text-center">{t("empty")}</p>
      )}

      {(isLoading || isFetchingNextPage) && (
        <div className="flex flex-col gap-2">
          {Array.from({ length: 4 }).map((_, i) => (
            <SubmissionCardSkeleton key={`submission-skeleton-${i}`} />
          ))}
        </div>
      )}

      {!isLoading && !hasNextPage && (
        <p className="text-muted-foreground p-4 text-center text-sm">
          {t("end")}
        </p>
      )}

      <ScrollToTopButton />
    </section>
  );
};
