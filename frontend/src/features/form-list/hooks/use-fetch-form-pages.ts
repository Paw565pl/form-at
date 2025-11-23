import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormFilterOptionsDto,
  FormListResponseDto,
  FormSortOptions,
} from "@/core/types/form";
import { PaginatedResponseDto } from "@/core/types/paginated-response-dto";
import { SortOptionsDto } from "@/core/types/sort-options-dto";
import {
  infiniteQueryOptions,
  QueryClient,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchFormPagesQueryOptions = (
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsDto?: SortOptionsDto<FormSortOptions>,
) =>
  infiniteQueryOptions<
    PaginatedResponseDto<FormListResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: ["forms", formFilterOptionsDto, formSortOptionsDto] as const,
    queryFn: async ({ pageParam }) => {
      const { data } = await apiService.get<
        PaginatedResponseDto<FormListResponseDto>
      >("/api/v1/forms", {
        params: {
          ...formFilterOptionsDto,
          ...formSortOptionsDto,
          page: pageParam,
        },
      });
      return data;
    },
    initialPageParam: 0,
    getNextPageParam: ({ page }) =>
      page.number + 1 < page.totalPages ? page.number + 1 : undefined,
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchFormPages = (
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsDto?: SortOptionsDto<FormSortOptions>,
) =>
  useInfiniteQuery(
    getFetchFormPagesQueryOptions(formFilterOptionsDto, formSortOptionsDto),
  );

export const prefetchFormPages = (
  queryClient: QueryClient,
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsDto?: SortOptionsDto<FormSortOptions>,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormPagesQueryOptions(formFilterOptionsDto, formSortOptionsDto),
  );
