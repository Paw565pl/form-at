import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { shuffleFormData } from "@/core/utils/shuffle-form-data";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { Submission } from "@/features/submission-create/components/submission";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";
import { getFetchPrivateFormDetailsQueryOptions } from "../form-details/private-form/hooks/use-fetch-private-form-details";

export const SubmissionCreatePage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/new">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);
  const formData = queryClient.getQueryData<FormDetailResponseDto>(
    getFetchPrivateFormDetailsQueryOptions(slug, "").queryKey,
  );

  if (!formData) return notFound();

  const preparedFormData = shuffleFormData(formData!);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Submission formData={preparedFormData} />
    </HydrationBoundary>
  );
};
