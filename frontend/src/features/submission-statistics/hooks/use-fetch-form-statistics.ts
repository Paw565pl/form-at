import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionStatisticsResponseDto } from "@/core/types/submission";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchFormStatisticsQueryOptions = (
  idOrSlug: string,
  options?: Omit<
    UseQueryOptions<
      SubmissionStatisticsResponseDto[],
      AxiosError<ErrorResponseDto>
    >,
    "queryKey"
  >,
) =>
  queryOptions<SubmissionStatisticsResponseDto[], AxiosError<ErrorResponseDto>>(
    {
      queryKey: ["forms", idOrSlug, "submissions", "statistics"] as const,
      queryFn: async () => {
        const { data } = await apiService.get<
          SubmissionStatisticsResponseDto[]
        >(`/api/v1/forms/${idOrSlug}/submissions/statistics`);
        return data;
      },
      staleTime: 1000 * 60 * 10, // 10 minutes
      ...options,
    },
  );

export const useFetchFormStatistics = (
  idOrSlug: string,
  options?: Omit<
    UseQueryOptions<
      SubmissionStatisticsResponseDto[],
      AxiosError<ErrorResponseDto>
    >,
    "queryKey"
  >,
) => useQuery(getFetchFormStatisticsQueryOptions(idOrSlug, options));

export const prefetchFormStatistics = (
  queryClient: QueryClient,
  idOrSlug: string,
) => queryClient.prefetchQuery(getFetchFormStatisticsQueryOptions(idOrSlug));
