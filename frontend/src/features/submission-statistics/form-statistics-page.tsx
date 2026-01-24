import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { Statistics } from "@/features/submission-statistics/components/statistics";
import { prefetchFormStatistics } from "@/features/submission-statistics/hooks/use-fetch-form-statistics";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormStatisticsPage = async ({
  params,
}: PageProps<"/forms/[slug]/statistics">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchFormStatistics(queryClient, slug),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Statistics formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
