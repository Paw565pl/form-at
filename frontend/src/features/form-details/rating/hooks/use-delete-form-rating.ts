import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteFormRating = (formIdOrSlug: string) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>>({
    mutationKey: ["forms", formIdOrSlug, "rating", "delete"],
    mutationFn: async () => {
      await apiService.delete(`/api/v1/forms/${formIdOrSlug}/rating`);
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms"],
      });
    },
  });
