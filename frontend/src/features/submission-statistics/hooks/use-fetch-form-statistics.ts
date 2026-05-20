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

const dataDiscoveryTime = new WeakMap<
  SubmissionStatisticsResponseDto[],
  number
>();

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
      queryKey: ["forms", formIdOrSlug, "submissions", "statistics"],
      queryFn: async () => {
        const { data } = await apiService.get<
          SubmissionStatisticsResponseDto[]
        >(`/api/v1/forms/${formIdOrSlug}/submissions/statistics`);
        return data;
      },
      staleTime: 1000 * 60 * 60, // 60 minutes
      refetchInterval: ({ state: { data, error } }) => {
        if (!data || error) return 2_000;

        let discoveredAt = dataDiscoveryTime.get(data);
        if (!discoveredAt) {
          discoveredAt = Date.now();
          dataDiscoveryTime.set(data, discoveredAt);
        }

        const unchangedDurationMs = Date.now() - discoveredAt;

        if (unchangedDurationMs > 60_000) return 10_000;
        if (unchangedDurationMs > 15_000) return 5_000;

        return 2_000;
      },
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
