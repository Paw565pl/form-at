import { authenticatedApiService } from "@/core/services/api-service";
import { minioService } from "@/core/services/minio-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormDetailResponseDto,
  FormRequest,
  FormRequestDto,
} from "@/core/types/form";

import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const useCreateForm = () =>
  useMutation<
    FormDetailResponseDto,
    AxiosError<ErrorResponseDto> | Error,
    FormRequest
  >({
    mutationKey: ["forms", "create"] as const,
    mutationFn: async (request) => {
      const files: File[] = [
        request.thumbnail,
        ...request.questions.map((q) => q.image),
      ].filter((f) => f !== null);

      const result = await minioService.upload(files);
      if (!result.isSuccess) throw result.error;

      const requestDto: FormRequestDto = {
        ...request,
        thumbnailKey: request.thumbnail
          ? (result.filesToKeys.get(request.thumbnail) ?? null)
          : null,
        questions: request.questions.map((q) => ({
          ...q,
          imageKey: q.image ? (result.filesToKeys.get(q.image) ?? null) : null,
        })),
      };
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
