import { apiService } from "@/core/services/api-service";
import { minioService } from "@/core/services/minio-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormDetailResponseDto,
  FormRequest,
  FormRequestDto,
} from "@/core/types/form";
import { getFetchFormDetailsQueryOptions } from "@/features/form-details/hooks/use-fetch-form-details";
import { getFetchFormStatisticsQueryOptions } from "@/features/submission-statistics/hooks/use-fetch-form-statistics";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useState } from "react";

const resolveFileKey = (
  urlOrFile: File | string | null,
  filesToKeys: Readonly<Map<File, string>>,
): string | null => {
  if (urlOrFile === null) return null;
  if (urlOrFile instanceof File) return filesToKeys.get(urlOrFile) ?? null;

  // parse url and return key only
  try {
    const url = new URL(urlOrFile);
    const key = url.pathname.split("/").slice(2).join("/");

    return key;
  } catch {
    return null;
  }
};

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
      ].filter((f) => f instanceof File);

      const result = await minioService.upload(files, (percent) =>
        setUploadProgressPercent(percent),
      );
      if (!result.isSuccess) throw result.error;

      const requestDto: FormRequestDto = {
        ...request,
        thumbnailKey: resolveFileKey(request.thumbnail, result.filesToKeys),
        questions: request.questions.map((q) => ({
          ...q,
          imageKey: resolveFileKey(q.image, result.filesToKeys),
        })),
      };
      const { data } = await apiService.put(
        `/api/v1/forms/${idOrSlug}`,
        requestDto,
      );

      return data;
    },
    onSettled: (_, __, ___, _____, { client }) => {
      setUploadProgressPercent(null);
      client.invalidateQueries({ queryKey: ["forms"] });
      client.invalidateQueries({
        queryKey: getFetchFormDetailsQueryOptions(idOrSlug).queryKey,
      });
      client.invalidateQueries({
        queryKey: getFetchFormStatisticsQueryOptions(idOrSlug).queryKey,
      });
    },
  });

  return { ...mutation, uploadProgressPercent };
};
