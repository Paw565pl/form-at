import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { StatisticsResponseDto } from "@/core/types/statistics";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchStatistics = (
  options?: Omit<
    UseQueryOptions<StatisticsResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryOptions<StatisticsResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["statistics"] as const,
    queryFn: async () => {
      const { data } =
        await apiService.get<StatisticsResponseDto>(`/api/v1/statistics`);
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
    ...options,
  });

export const useFetchStatistics = (
  options?: Omit<
    UseQueryOptions<StatisticsResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) => useQuery(getFetchStatistics(options));

export const prefetchStatistics = (queryClient: QueryClient) =>
  queryClient.prefetchQuery(getFetchStatistics());
