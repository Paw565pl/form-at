import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormRatingRequestDto,
  FormRatingResponseDto,
} from "@/core/types/rating";

import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateFormRating = (formIdOrSlug: string) =>
  useMutation<
    FormRatingResponseDto,
    AxiosError<ErrorResponseDto>,
    FormRatingRequestDto
  >({
    mutationKey: ["forms", formIdOrSlug, "rating", "create"],
    mutationFn: async (request) => {
      const { data } = await apiService.post<FormRatingResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/rating`,
        request,
      );
      return data;
    },
    onSuccess: (_, __, ___, { client }) => {
      client.invalidateQueries({
        queryKey: ["forms"],
      });
    },
  });
