"use client";

import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { shuffleFormData } from "@/core/utils/shuffle-form-data";
import { getFetchFormDetailsQueryOptions } from "@/features/form-details/hooks/use-fetch-form-details";
import { getFetchPrivateFormDetailsQueryOptions } from "@/features/form-details/private-form/hooks/use-fetch-private-form-details";
import dynamic from "next/dynamic";
import { useRouter } from "next/navigation";
import { useMemo } from "react";

interface SubmissionCreateGateProps {
  readonly slug: string;
}

const Submission = dynamic(
  () =>
    import("@/features/submission-create/components/submission").then(
      (m) => m.Submission,
    ),
  { ssr: false },
);

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
  if (!formData) router.replace(`/forms/${slug}`);

  const preparedFormData = useMemo(
    () => (formData ? shuffleFormData(formData) : null),
    [formData],
  );

  if (!preparedFormData) return null;

  return <Submission formData={preparedFormData} />;
};
