import { getQueryClient } from "@/core/lib/tanstack-query";
import { Home } from "@/features/home/components/home";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { prefetchStatistics } from "@/features/home/hooks/use-fetch-statistics";

export const HomePage = async () => {
  const queryClient = getQueryClient();
  await prefetchStatistics(queryClient);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Home />
    </HydrationBoundary>
  );
};
