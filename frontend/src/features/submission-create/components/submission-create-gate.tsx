"use client";

import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { shuffleFormData } from "@/core/utils/shuffle-form-data";
import { getFetchFormDetailsQueryOptions } from "@/features/form-details/hooks/use-fetch-form-details";
import { getFetchPrivateFormDetailsQueryOptions } from "@/features/form-details/private-form/hooks/use-fetch-private-form-details";
import { Submission } from "@/features/submission-create/components/submission";
import { notFound, useRouter } from "next/navigation";
import { useEffect, useMemo } from "react";

interface SubmissionCreateGateProps {
  readonly slug: string;
}

export const SubmissionCreateGate = ({ slug }: SubmissionCreateGateProps) => {
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
  }, [formData, router, slug]);

  const prepared = useMemo(
    () => (formData ? shuffleFormData(formData) : null),
    [formData],
  );

  if (!prepared) return notFound();

  return <Submission formData={prepared} />;
};
