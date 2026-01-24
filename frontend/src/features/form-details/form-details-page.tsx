import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { prefetchFormCommentsPages } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { Form } from "@/features/form-details/components/form";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { prefetchMySubmission } from "@/features/form-details/my-submission/hooks/use-fetch-my-submission";
import { PrivateForm } from "@/features/form-details/private-form/private-form";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";

export const FormDetailsPage = async ({
  params,
}: PageProps<"/forms/[slug]">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await Promise.all([
    prefetchFormDetails(queryClient, slug),
    prefetchFormCommentsPages(queryClient, slug),
    prefetchMySubmission(queryClient, slug),
  ]);

  const publicForm = queryClient.getQueryData<FormDetailResponseDto>(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      {publicForm ? (
        <Form formIdOrSlug={slug} />
      ) : (
        <PrivateForm formIdOrSlug={slug} />
      )}
    </HydrationBoundary>
  );
};
