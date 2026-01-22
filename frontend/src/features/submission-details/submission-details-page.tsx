import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { SubmissionDetails } from "@/features/submission-details/components/submission-details";
import { prefetchSubmissionDetails } from "@/features/submission-details/hooks/use-fetch-submission-details";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const SubmissionDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/[submissionId]">) => {
  const { slug, submissionId } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchSubmissionDetails(queryClient, slug, submissionId),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <SubmissionDetails formIdOrSlug={slug} submissionId={submissionId} />
    </HydrationBoundary>
  );
};
