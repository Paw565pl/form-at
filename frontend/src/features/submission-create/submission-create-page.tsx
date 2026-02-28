import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { SubmissionCreateGate } from "@/features/submission-create/components/submission-create-gate";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const SubmissionCreatePage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/new">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <SubmissionCreateGate slug={slug} />
    </HydrationBoundary>
  );
};
