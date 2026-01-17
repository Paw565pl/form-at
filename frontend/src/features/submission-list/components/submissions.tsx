"use client";

import { ScrollToTopButton } from "@/core/components/scroll-to-top-button/scroll-to-top-button";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { useTranslations } from "next-intl";
import { useFetchSubmissionPages } from "../hooks/use-fetch-submission-pages";
import { ListView } from "./list-view";
import { SubmissionResponseDto } from "@/core/types/submission";

interface SubmissionsProps {
  readonly formIdOrSlug: string;
}

export const Submissions = ({ formIdOrSlug }: SubmissionsProps) => {
  const t = useTranslations("formListPage");
  const { data: submissionPages, error } =
    useFetchSubmissionPages(formIdOrSlug);

  const { data: formData } = useFetchFormDetails(formIdOrSlug);
  console.log(formData);
  if (error) throw error;
  if (!formData) return <p>{t("loading")}</p>;

  const totalElements = submissionPages?.pages.at(0)?.page.totalElements || 0;
  console.log(submissionPages);



  return (
    <section
      id="forms-list"
      className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
    >
      <header className="mb-2 flex flex-wrap items-center justify-between gap-4">
        <h1 className="ml-4 text-xl font-bold">
          {/* {t("title", { count: totalElements })} */}
          Showing {` ${totalElements} `}submissions for {formData.name}
        </h1>
      </header>

      <ListView formIdOrSlug={formIdOrSlug} />

      <ScrollToTopButton />
    </section>
  );
};
