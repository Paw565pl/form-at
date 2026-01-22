import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { SubmissionResponseDto } from "@/core/types/submission";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";
import { SubmissionDetails } from "@/features/submission-details/components/submission-details";
import {
  getFetchSubmissionDetailsQueryOptions,
  prefetchSubmissionDetails,
} from "@/features/submission-details/hooks/use-fetch-submission-details";

export const SubmissionDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/[submissionId]">) => {
  const { slug, submissionId } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);
  await prefetchSubmissionDetails(queryClient, slug, submissionId);

  const formData = queryClient.getQueryData<FormDetailResponseDto>(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );
  const submissionData = queryClient.getQueryData<SubmissionResponseDto>(
    getFetchSubmissionDetailsQueryOptions(slug, submissionId).queryKey,
  );

  if (!formData || !submissionData) return notFound();

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <SubmissionDetails formData={formData} submissionData={submissionData} />
    </HydrationBoundary>
  );
};
