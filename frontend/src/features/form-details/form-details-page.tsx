import { getQueryClient } from "@/core/lib/tanstack-query";
import { Form } from "@/features/form-details/components/form";
import { prefetchFormDetails } from "@/features/form-list/hooks/use-fetch-form-details";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Form formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
