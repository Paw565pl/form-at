import { getQueryClient } from "@/core/lib/tanstack-query";
import { auth } from "@/features/auth/config/auth-config";
import { Role } from "@/features/auth/types/role";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { Submissions } from "@/features/submission-list/components/submissions";
import { prefetchSubmissionPages } from "@/features/submission-list/hooks/use-fetch-submission-pages";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";

export const SubmissionListPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions">) => {
  const session = await auth();
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchSubmissionPages(queryClient, slug),
    prefetchFormDetails(queryClient, slug),
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
      <Submissions formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
