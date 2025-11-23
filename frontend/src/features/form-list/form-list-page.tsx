import { getQueryClient } from "@/core/lib/tanstack-query";
import { Forms } from "@/features/form-list/components/forms";
import { prefetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import {
  loadFormFilterSearchParams,
  loadFormSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormListPage = async ({ searchParams }: PageProps<"/forms">) => {
  const queryClient = getQueryClient();

  const [filtersDto, sortDto] = await Promise.all([
    loadFormFilterSearchParams(searchParams),
    loadFormSortSearchParams(searchParams),
  ]);
  await prefetchFormPages(queryClient, filtersDto, sortDto);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <section
        id="forms-list"
        className="flex w-full flex-col gap-2 px-5 py-10 lg:px-30"
      >
        <Forms />
      </section>
    </HydrationBoundary>
  );
};
