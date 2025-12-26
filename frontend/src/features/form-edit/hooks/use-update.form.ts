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
import { useState } from "react";

export const useUpdateForm = (idOrSlug: string) => {
  const [uploadProgressPercent, setUploadProgressPercent] = useState<
    number | null
  >(null);

  const mutation = useMutation<
    FormDetailResponseDto,
    AxiosError<ErrorResponseDto> | Error,
    FormRequest
  >({
    mutationKey: ["forms", idOrSlug, "update"] as const,
    mutationFn: async (request) => {
      const files: File[] = [
        request.thumbnail,
        ...request.questions.map((q) => q.image),
      ].filter((f) => f !== null);

      const result = await minioService.upload(files, (percent) =>
        setUploadProgressPercent(percent),
      );
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
      const { data } = await authenticatedApiService.put(
        `/api/v1/forms/${idOrSlug}`,
        requestDto,
      );

      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      setUploadProgressPercent(null);
      client.invalidateQueries({ queryKey: ["forms"] });
    },
  });

  return { ...mutation, uploadProgressPercent };
};
