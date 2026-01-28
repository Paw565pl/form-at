"use client";

import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { shuffleFormData } from "@/core/utils/shuffle-form-data";
import { getFetchFormDetailsQueryOptions } from "@/features/form-details/hooks/use-fetch-form-details";
import { getFetchPrivateFormDetailsQueryOptions } from "@/features/form-details/private-form/hooks/use-fetch-private-form-details";
import { Submission } from "@/features/submission-create/components/submission";
import dynamic from "next/dynamic";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

interface SubmissionCreateGateProps {
  readonly slug: string;
}

const SubmissionCreateGateInternal = ({ slug }: SubmissionCreateGateProps) => {
  const queryClient = getQueryClient();
  const router = useRouter();

  const privateForm = queryClient.getQueryData<FormDetailResponseDto>(
    getFetchPrivateFormDetailsQueryOptions(slug, "").queryKey,
  );
  const publicForm = queryClient.getQueryData<FormDetailResponseDto>(
    getFetchFormDetailsQueryOptions(slug).queryKey,
  );

  const formData = privateForm ?? publicForm;

  useEffect(() => {
    if (!formData) router.replace(`/forms/${slug}`);
  }, [slug, router, formData]);

  if (!formData) return null;
  const shuffledFormData = shuffleFormData(formData);

  return <Submission formData={shuffledFormData} />;
};

export const SubmissionCreateGate = dynamic(
  () => Promise.resolve(SubmissionCreateGateInternal),
  {
    ssr: false,
  },
);
