import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form-detail-response-dto";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchFormDetailQueryOptions = (idOrSlug: string) =>
  queryOptions<FormDetailResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", idOrSlug] as const,
    queryFn: async () => {
      const { data } = await apiService.get<FormDetailResponseDto>(
        `/api/v1/forms/${idOrSlug}`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchFormDetail = (idOrSlug: string) =>
  useQuery(getFetchFormDetailQueryOptions(idOrSlug));

export const prefetchFormDetail = (
  queryClient: QueryClient,
  idOrSlug: string,
) => queryClient.prefetchQuery(getFetchFormDetailQueryOptions(idOrSlug));
