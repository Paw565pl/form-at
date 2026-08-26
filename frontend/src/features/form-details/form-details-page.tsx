import { getQueryClient } from "@/core/lib/tanstack-query";
import { prefetchFormCommentPages } from "@/features/form-details/comments/hooks/use-fetch-form-comment-pages";
import { Form } from "@/features/form-details/components/form";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
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
    prefetchFormCommentPages(queryClient, slug),
  ]);

  const publicFormQueryState = queryClient.getQueryState(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );
  const isPrivateForm =
    publicFormQueryState?.error?.status === HttpStatusCode.Forbidden;

  const component = isPrivateForm ? (
    <PrivateForm formIdOrSlug={slug} />
  ) : (
    <Form formIdOrSlug={slug} />
  );

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      {component}
    </HydrationBoundary>
  );
};
