import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteSubmission = (
  formIdOrSlug: string,
  submissionId: string,
) =>
  useMutation<SubmissionResponseDto, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: [
      "forms",
      formIdOrSlug,
      "submissions",
      submissionId,
      "delete",
    ] as const,
    mutationFn: async () => {
      const { data } = await apiService.delete(
        `/api/v1/forms/${formIdOrSlug}/submissions/${submissionId}`,
      );
      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms", formIdOrSlug, "submissions"],
      });
    },
  });
