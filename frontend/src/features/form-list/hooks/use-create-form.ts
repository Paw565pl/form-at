import { authenticatedApiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form-detail-response-dto";
import { FormRequestDto } from "@/core/types/form-request-dto";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateForm = () =>
  useMutation<
    FormDetailResponseDto,
    AxiosError<ErrorResponseDto>,
    FormRequestDto
  >({
    mutationKey: ["forms", "create"] as const,
    mutationFn: async (requestDto) => {
      const { data } = await authenticatedApiService.post(
        "/api/v1/forms",
        requestDto,
      );
      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      client.invalidateQueries({ queryKey: ["forms"] });
    },
  });
