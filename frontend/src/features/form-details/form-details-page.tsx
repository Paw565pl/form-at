import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormCommentsPages } from "@/features/form-details/comments/hooks/use-fetch-form-comments-pages";
import { Form } from "@/features/form-details/components/form";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { prefetchMySubmission } from "@/features/form-details/my-submission/hooks/use-fetch-my-submission";
import { PrivateForm } from "@/features/form-details/private-form/private-form";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { HttpStatusCode } from "axios";

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

  const publicFormQueryState = queryClient.getQueryState(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );
  const isPrivateForm =
    publicFormQueryState?.error?.status === HttpStatusCode.Unauthorized ||
    publicFormQueryState?.error?.status === HttpStatusCode.Forbidden;

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      {isPrivateForm ? (
        <PrivateForm formIdOrSlug={slug} />
      ) : (
        <Form formIdOrSlug={slug} />
      )}
    </HydrationBoundary>
  );
};
