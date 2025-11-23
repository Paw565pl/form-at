import { getQueryClient } from "@/core/lib/tanstack-query";
import { Forms } from "@/features/form-list/components/forms";
import { prefetchFormPage } from "@/features/form-list/hooks/use-fetch-form-page";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormListPage = async () => {
  const queryClient = getQueryClient();

  await prefetchFormPage(queryClient);

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
