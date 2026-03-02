import { getQueryClient } from "@/core/lib/tanstack-query";
import { Home } from "@/features/home/components/home";
import { prefetchStatistics } from "@/features/home/hooks/use-fetch-statistics";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const HomePage = async () => {
  const queryClient = getQueryClient();
  await prefetchStatistics(queryClient);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Home />
    </HydrationBoundary>
  );
};
