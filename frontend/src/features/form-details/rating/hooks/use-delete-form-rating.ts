import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { getFetchFormDetailsQueryOptions } from "@/features/form-details/hooks/use-fetch-form-details";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteFormRating = (formIdOrSlug: string) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>>({
    mutationKey: ["forms", formIdOrSlug, "rating", "delete"] as const,
    mutationFn: async () => {
      await apiService.delete(`/api/v1/forms/${formIdOrSlug}/rating`);
    },
    onSettled: (_, __, ___, ____, { client }) => {
      client.invalidateQueries({
        queryKey: getFetchFormDetailsQueryOptions(formIdOrSlug).queryKey,
      });
    },
  });
