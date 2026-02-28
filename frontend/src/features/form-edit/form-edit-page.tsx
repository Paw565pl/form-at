import { getQueryClient } from "@/core/lib/tanstack-query";
import { auth } from "@/features/auth/config/auth-config";
import {
  getFetchFormDetailsQueryOptions,
  prefetchFormDetails,
} from "@/features/form-details/hooks/use-fetch-form-details";
import { FormEditForm } from "@/features/form-edit/components/form-edit-form";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";

export const FormEditPage = async ({
  params,
}: PageProps<"/forms/[slug]/edit">) => {
  const session = await auth();
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);

  const formDetails = queryClient.getQueryData(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );
  const isFormAuthor = session?.user.name === formDetails?.authorName;
  if (!isFormAuthor) return notFound();

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <FormEditForm slug={slug} />
    </HydrationBoundary>
  );
};
