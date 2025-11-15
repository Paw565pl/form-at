import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form-detail-response-dto";
import { FormRequestDto } from "@/core/types/form-request-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useUpdateForm = (idOrSlug: string) =>
  useMutation<
    FormDetailResponseDto,
    AxiosError<ErrorResponseDto>,
    FormRequestDto
  >({
    mutationKey: ["forms", idOrSlug, "update"] as const,
    mutationFn: async (requestDto) => {
      const { data } = await authenticatedApiService.put(
        `/api/v1/forms/${idOrSlug}`,
        requestDto,
      );
      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      client.invalidateQueries({ queryKey: ["forms"] });
    },
  });
