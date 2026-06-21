import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionsStatisticsResponseDto } from "@/core/types/submission-statistics";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

const dataDiscoveryTime = new WeakMap<
  SubmissionsStatisticsResponseDto,
  number
>();

export const getFetchFormSubmissionsStatisticsQueryOptions = (
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<
      SubmissionsStatisticsResponseDto,
      AxiosError<ErrorResponseDto>
    >,
    "queryKey"
  >,
) =>
  queryOptions<SubmissionsStatisticsResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", formIdOrSlug, "submissions", "statistics"],
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionsStatisticsResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/submissions/statistics`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 60, // 60 minutes
    refetchInterval: ({ state: { data, error } }) => {
      if (error) return false;
      if (!data) return 2_000;

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
  });

export const useFetchFormSubmissionsStatistics = (
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<
      SubmissionsStatisticsResponseDto,
      AxiosError<ErrorResponseDto>
    >,
    "queryKey"
  >,
) =>
  useQuery(
    getFetchFormSubmissionsStatisticsQueryOptions(formIdOrSlug, options),
  );

export const prefetchFormSubmissionsStatistics = (
  queryClient: QueryClient,
  formIdOrSlug: string,
) =>
  queryClient.prefetchQuery(
    getFetchFormSubmissionsStatisticsQueryOptions(formIdOrSlug),
  );
