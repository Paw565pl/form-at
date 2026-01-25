import { getQueryClient } from "@/core/lib/tanstack-query";
import { auth } from "@/features/auth/config/auth-config";
import { Role } from "@/features/auth/types/role";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { SubmissionDetails } from "@/features/submission-details/components/submission-details";
import { prefetchSubmissionDetails } from "@/features/submission-details/hooks/use-fetch-submission-details";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";

export const SubmissionDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/[submissionId]">) => {
  const session = await auth();
  const { slug, submissionId } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchSubmissionDetails(queryClient, slug, submissionId),
  ]);

  const formDetails = queryClient.getQueryData(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );

  const isFormAuthor = session?.user.name === formDetails?.authorName;
  const isAdmin = session?.user.roles.includes(Role.ADMIN);

  if ((!isFormAuthor && !isAdmin) || !formDetails?.saveSubmissions)
    return notFound();

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <SubmissionDetails formIdOrSlug={slug} submissionId={submissionId} />
    </HydrationBoundary>
  );
};
