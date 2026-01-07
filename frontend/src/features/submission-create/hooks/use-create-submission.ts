import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  SubmissionRequestDto,
  SubmissionResponseDto,
} from "@/core/types/submission";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateSubmission = (formIdOrSlug: string) => {
  const mutation = useMutation<
    SubmissionResponseDto,
    AxiosError<ErrorResponseDto>,
    SubmissionRequestDto
  >({
    mutationKey: ["submissions", formIdOrSlug, "create"] as const,
    mutationFn: async (request) => {
      const { data } = await authenticatedApiService.post(
        `/api/v1/forms/${formIdOrSlug}/submissions`,
        request,
      );

      return data;
    },
  });

  return mutation;
};
