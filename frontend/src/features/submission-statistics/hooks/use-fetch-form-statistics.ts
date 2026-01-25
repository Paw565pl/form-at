import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionStatisticsResponseDto } from "@/core/types/submission-statistics";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchFormStatisticsQueryOptions = (
  formIdOrSlug: string,
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
      queryKey: ["forms", formIdOrSlug, "submissions", "statistics"] as const,
      queryFn: async () => {
        const { data } = await apiService.get<
          SubmissionStatisticsResponseDto[]
        >(`/api/v1/forms/${formIdOrSlug}/submissions/statistics`);
        return data;
      },
      staleTime: 1000 * 60 * 10, // 10 minutes
      ...options,
    },
  );

export const useFetchFormStatistics = (
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<
      SubmissionStatisticsResponseDto[],
      AxiosError<ErrorResponseDto>
    >,
    "queryKey"
  >,
) => useQuery(getFetchFormStatisticsQueryOptions(formIdOrSlug, options));

export const prefetchFormStatistics = (
  queryClient: QueryClient,
  formIdOrSlug: string,
) =>
  queryClient.prefetchQuery(getFetchFormStatisticsQueryOptions(formIdOrSlug));
