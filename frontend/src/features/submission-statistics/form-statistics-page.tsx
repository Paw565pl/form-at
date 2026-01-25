import { getQueryClient } from "@/core/lib/tanstack-query";
import { auth } from "@/features/auth/config/auth-config";
import { Role } from "@/features/auth/types/role";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { Statistics } from "@/features/submission-statistics/components/statistics";
import { prefetchFormStatistics } from "@/features/submission-statistics/hooks/use-fetch-form-statistics";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";

export const FormStatisticsPage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/statistics">) => {
  const session = await auth();
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchFormStatistics(queryClient, slug),
  ]);

  const formDetails = queryClient.getQueryData(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );

  const isFormAuthor = session?.user.name === formDetails?.authorName;
  const isAdmin = session?.user.roles.includes(Role.ADMIN);

  if (!(isFormAuthor || isAdmin || formDetails?.saveSubmissions))
    return notFound();

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Statistics formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
