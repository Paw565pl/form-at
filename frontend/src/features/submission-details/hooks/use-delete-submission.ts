import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteSubmission = (
  formIdOrSlug: string,
  submissionId: string,
) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: ["forms", formIdOrSlug, "submissions", submissionId, "delete"],
    mutationFn: async () => {
      await apiService.delete<undefined>(
        `/api/v1/forms/${formIdOrSlug}/submissions/${submissionId}`,
      );
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms", formIdOrSlug, "submissions"],
      });
    },
  });
