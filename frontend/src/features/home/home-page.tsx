import { Home } from "@/features/home/components/home";

export const HomePage = async () => {
  // todo: load stats
  // const queryClient = getQueryClient();

  return (
    // <HydrationBoundary state={dehydrate(queryClient)}>
    <Home />
    // </HydrationBoundary>
  );
};
