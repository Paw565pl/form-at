import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormCommentsPages } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { Form } from "@/features/form-details/components/form";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchFormCommentsPages(queryClient, slug),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Form formIdOrSlug={slug} />
    </HydrationBoundary>
  );
};
