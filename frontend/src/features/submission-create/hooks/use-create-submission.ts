import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  SubmissionRequestDto,
  SubmissionResponseDto,
} from "@/core/types/submission";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateSubmission = (formIdOrSlug: string) =>
  useMutation<
    SubmissionResponseDto,
    AxiosError<ErrorResponseDto>,
    SubmissionRequestDto
  >({
    mutationKey: ["forms", formIdOrSlug, "submissions", "create"],
    mutationFn: async (request) => {
      const { data } = await apiService.post(
        `/api/v1/forms/${formIdOrSlug}/submissions`,
        request,
      );
      return data;
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms", formIdOrSlug, "submissions"],
      });
    },
  });
