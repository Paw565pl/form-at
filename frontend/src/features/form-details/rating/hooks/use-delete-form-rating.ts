import { getQueryClient } from "@/core/lib/tanstack-query";
import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteFormRating = (formIdOrSlug: string) => {
  const queryClient = getQueryClient();

  const mutation = useMutation<undefined, AxiosError<ErrorResponseDto>>({
    mutationKey: ["forms", formIdOrSlug, "rating", "delete"] as const,
    mutationFn: async () => {
      await apiService.delete(`/api/v1/forms/${formIdOrSlug}/rating`);
    },
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: ["forms", formIdOrSlug],
      });
    },
  });

  return mutation;
};
