import { getQueryClient } from "@/core/lib/tanstack-query";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { prefetchFormDetails } from "../form-details/hooks/use-fetch-form-details";
import { Submissions } from "./components/submissions";
import { prefetchSubmissionPages } from "./hooks/use-fetch-submission-pages";

export const SubmissionListPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchSubmissionPages(queryClient, slug),
    prefetchFormDetails(queryClient, slug),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Submissions formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
