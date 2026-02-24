"use client";

import { Comments } from "@/features/form-details/comments/comments";
import { Banner } from "@/features/form-details/components/banner";
import { Details } from "@/features/form-details/components/details";
import { QuestionList } from "@/features/form-details/components/question-list/question-list";
import { FormDetailsLoading } from "@/features/form-details/form-details-loading";
import { useFetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { HttpStatusCode } from "axios";
import { notFound } from "next/navigation";

interface FormProps {
  readonly formIdOrSlug: string;
}

export const Form = ({ formIdOrSlug }: FormProps) => {
  const { data: form, isLoading, error } = useFetchFormDetails(formIdOrSlug);

  if (error) {
    if (error.status === HttpStatusCode.NotFound) return notFound();
    else throw error;
  }

  if (!form || isLoading) return <FormDetailsLoading />;

  return (
    <section id="form-details" className="px-5 py-10 lg:px-30">
      <Banner form={form} />
      <Details form={form} />
      <QuestionList form={form} />
      <Comments formIdOrSlug={formIdOrSlug} />
    </section>
  );
};
