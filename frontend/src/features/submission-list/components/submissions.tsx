"use client";

import { ScrollToTopButton } from "@/core/components/scroll-to-top-button/scroll-to-top-button";
import { Card } from "@/core/components/ui/card";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { useFormatter, useTranslations } from "next-intl";
import Link from "next/link";
import InfiniteScroll from "react-infinite-scroll-component";
import { useFetchSubmissionPages } from "../hooks/use-fetch-submission-pages";

interface SubmissionsProps {
  readonly formIdOrSlug: string;
}

export const Submissions = ({ formIdOrSlug }: SubmissionsProps) => {
  const t = useTranslations("submissionListPage");
  const format = useFormatter();
  const {
    data: submissionPages,
    error,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useFetchSubmissionPages(formIdOrSlug);
  const { data: formData } = useFetchFormDetails(formIdOrSlug);

  if (error) throw error;

  const totalElements = submissionPages?.pages.at(0)?.page.totalElements || 0;

  const dataLength =
    submissionPages?.pages.reduce(
      (acc, curr) => acc + curr.content.length,
      0,
    ) || 0;

  return (
    <section
      id="forms-list"
      className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">
          {t("title", { count: totalElements, formName: formData.name })}
        </h1>
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
                className="hover:border-primary flex-row justify-between p-4 transition-all"
              >
                <p className="font-semibold">AUTHORNAME</p>
                <span className="text-muted-foreground">
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

      {(isLoading || isFetchingNextPage) && <p>{t("loading")}</p>}

      {!isLoading && !hasNextPage && (
        <p className="text-muted-foreground p-4 text-center text-sm">
          {t("end")}
        </p>
      )}

      <ScrollToTopButton />
    </section>
  );
};
