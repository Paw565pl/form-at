import { getQueryClient } from "@/core/lib/tanstack-query";
import { auth } from "@/features/auth/config/auth-config";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { prefetchMySubmission } from "@/features/form-details/my-submission/hooks/use-fetch-my-submission";
import { SubmissionCreateGate } from "@/features/submission-create/components/submission-create-gate";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const SubmissionCreatePage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/new">) => {
  const { slug } = await params;
  const session = await auth();

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchMySubmission(queryClient, slug, { enabled: !!session }),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <SubmissionCreateGate slug={slug} />
    </HydrationBoundary>
  );
};
