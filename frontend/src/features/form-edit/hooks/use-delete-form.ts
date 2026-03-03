import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteForm = (idOrSlug: string) =>
  useMutation<undefined, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: ["forms", idOrSlug, "delete"],
    mutationFn: async () => {
      const { data } = await apiService.delete<undefined>(
        `/api/v1/forms/${idOrSlug}`,
      );
      return data;
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms"],
        refetchType: "inactive",
      });
    },
  });
