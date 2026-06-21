import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form";
import { queryOptions, useQuery, UseQueryOptions } from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchPrivateFormDetailsQueryOptions = (
  idOrSlug: string,
  password: string,
  options?: Omit<
    UseQueryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  // excluding password for security
  // eslint-disable-next-line @tanstack/query/exhaustive-deps
  queryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug, "access"],
    queryFn: async () => {
      const { data } = await apiService.post<FormDetailResponseDto>(
        `/api/v1/forms/${idOrSlug}/access`,
        { password },
      );
      return data;
    },
    staleTime: 1000 * 60 * 1, // 1 minute
    ...options,
  });

export const useFetchPrivateFormDetails = (
  idOrSlug: string,
  password: string,
  options?: Omit<
    UseQueryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  useQuery(getFetchPrivateFormDetailsQueryOptions(idOrSlug, password, options));
