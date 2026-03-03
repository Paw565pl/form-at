import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchFormDetailsQueryOptions = (
  idOrSlug: string,
  options?: Omit<
    UseQueryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug],
    queryFn: async () => {
      const { data } = await apiService.get<FormDetailResponseDto>(
        `/api/v1/forms/${idOrSlug}`,
      );
      return data;
    },
    ...options,
  });

export const useFetchFormDetails = (
  idOrSlug: string,
  options?: Omit<
    UseQueryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) => useQuery(getFetchFormDetailsQueryOptions(idOrSlug, options));

export const prefetchFormDetails = (
  queryClient: QueryClient,
  idOrSlug: string,
) => queryClient.prefetchQuery(getFetchFormDetailsQueryOptions(idOrSlug));
