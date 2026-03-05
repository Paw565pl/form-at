import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { StatisticsResponseDto } from "@/core/types/statistics";
import { QueryClient, queryOptions, useQuery } from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchStatisticsQueryOptions = () =>
  queryOptions<StatisticsResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["statistics"],
    queryFn: async () => {
      const { data } =
        await apiService.get<StatisticsResponseDto>(`/api/v1/statistics`);
      return data;
    },
  });

export const useFetchStatistics = () =>
  useQuery(getFetchStatisticsQueryOptions());

export const prefetchStatistics = (queryClient: QueryClient) =>
  queryClient.prefetchQuery(getFetchStatisticsQueryOptions());
