import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { FormDetailResponseDto } from "@/core/types/form";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchFormDetailsQueryOptions = (idOrSlug: string) =>
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

export const useFetchFormDetails = (idOrSlug: string) =>
  useQuery(getFetchFormDetailsQueryOptions(idOrSlug));

export const prefetchFormDetails = (
  queryClient: QueryClient,
  idOrSlug: string,
) => queryClient.prefetchQuery(getFetchFormDetailsQueryOptions(idOrSlug));
