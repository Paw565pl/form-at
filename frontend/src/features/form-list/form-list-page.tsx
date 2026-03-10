import { getQueryClient } from "@/core/lib/tanstack-query";
import { Forms } from "@/features/form-list/components/forms";
import { FORM_LIST_LAYOUT_COOKIE_KEY } from "@/features/form-list/constants/form-list-layout-cookie-key";
import { prefetchFormPages } from "@/features/form-list/hooks/use-fetch-form-pages";
import {
  loadFormFilterSearchParams,
  loadFormSortSearchParams,
} from "@/features/form-list/search-params/form-search-params";

import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { cookies } from "next/headers";

export const FormListPage = async ({ searchParams }: PageProps<"/forms">) => {
  const queryClient = getQueryClient();

  const [filtersDto, sortDto] = await Promise.all([
    loadFormFilterSearchParams(searchParams),
    loadFormSortSearchParams(searchParams),
  ]);
  await prefetchFormPages(queryClient, filtersDto, sortDto);

  const cookieStore = await cookies();
  const initialFormListLayout = cookieStore.get(
    FORM_LIST_LAYOUT_COOKIE_KEY,
  )?.value;

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Forms initialFormListLayout={initialFormListLayout} />
    </HydrationBoundary>
  );
};
