import { getQueryClient } from "@/core/lib/tanstack-query";
import { FormDetailResponseDto } from "@/core/types/form";
import { prefetchFormDetails } from "@/features/form-details/hooks/use-fetch-form-details";
import { Submission } from "@/features/submission-create/components/submission";
import { dehydrate, HydrationBoundary } from "@tanstack/react-query";
import { notFound } from "next/navigation";

const shuffleArray = <T,>(array: T[]): T[] => {
  const arr = [...array];
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
};

const prepareFormData = (
  formData: FormDetailResponseDto,
): FormDetailResponseDto => {
  switch (formData.shuffleVariant) {
    case "QUESTIONS":
      return {
        ...formData,
        questions: shuffleArray(formData.questions),
      };
    case "ANSWERS":
      return {
        ...formData,
        questions: formData.questions.map((q) => ({
          ...q,
          answers: shuffleArray(q.answers),
        })),
      };
    case "ALL":
      return {
        ...formData,
        questions: shuffleArray(
          formData.questions.map((q) => ({
            ...q,
            answers: shuffleArray(q.answers),
          })),
        ),
      };
    default:
      return formData;
  }
};

export const SubmissionCreatePage = async ({
  params,
}: PageProps<"/forms/[slug]/submissions/new">) => {
  const { slug } = await params;

  const queryClient = getQueryClient();
  await prefetchFormDetails(queryClient, slug);
  const formData = queryClient.getQueryData<FormDetailResponseDto>([
    "forms",
    slug,
  ]);

  if (!formData) return notFound();

  const preparedFormData = prepareFormData(formData);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <Submission formData={preparedFormData} />
    </HydrationBoundary>
  );
};
