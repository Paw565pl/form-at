import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form-detail-response-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useDeleteForm = (idOrSlug: string) =>
  useMutation<FormDetailResponseDto, AxiosError<ErrorResponseDto>, undefined>({
    mutationKey: ["forms", idOrSlug, "delete"] as const,
    mutationFn: async () => {
      const { data } = await authenticatedApiService.delete(
        `/api/v1/forms/${idOrSlug}`,
      );
      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      client.invalidateQueries({ queryKey: ["forms"] });
    },
  });
