import { apiService } from "@/core/services/api-service";
import { uploadService } from "@/core/services/upload-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormDetailResponseDto,
  FormRequest,
  FormRequestDto,
} from "@/core/types/form";

import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useState } from "react";

export const useCreateForm = () => {
  const [uploadProgressPercent, setUploadProgressPercent] = useState<
    number | null
  >(null);

  const mutation = useMutation<
    FormDetailResponseDto,
    AxiosError<ErrorResponseDto> | Error,
    FormRequest
  >({
    mutationKey: ["forms", "create"] as const,
    mutationFn: async (request) => {
      const files: File[] = [
        request.thumbnail,
        ...request.questions.map((q) => q.image),
      ].filter((f) => f instanceof File);

      const result = await uploadService.upload(files, (percent) =>
        setUploadProgressPercent(percent),
      );
      if (!result.isSuccess) throw result.error;

      const requestDto: FormRequestDto = {
        ...request,
        thumbnailKey:
          request.thumbnail instanceof File
            ? (result.filesToKeys.get(request.thumbnail) ?? null)
            : null,
        questions: request.questions.map((q) => ({
          ...q,
          imageKey:
            q.image instanceof File
              ? (result.filesToKeys.get(q.image) ?? null)
              : null,
        })),
      };
      const { data } = await apiService.post("/api/v1/forms", requestDto);

      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      setUploadProgressPercent(null);
      client.invalidateQueries({ queryKey: ["forms"] });
    },
  });

  return { ...mutation, uploadProgressPercent };
};
