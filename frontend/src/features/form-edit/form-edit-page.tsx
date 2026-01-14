import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { FormEditForm } from "@/features/form-edit/components/form-edit-form";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormEditPage = async ({
  params,
}: PageProps<"/forms/[slug]/edit">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <FormEditForm slug={slug} />
    </HydrationBoundary>
  );
};
