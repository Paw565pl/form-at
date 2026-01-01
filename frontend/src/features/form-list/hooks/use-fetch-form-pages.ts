import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormFilterOptionsDto,
  FormListResponseDto,
  FormSortOption,
} from "@/core/types/form";
import { PageOptionsDto } from "@/core/types/page-options-dto";
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
  formSortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  infiniteQueryOptions<
    PaginatedResponseDto<FormListResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: [
      "forms",
      formFilterOptionsDto,
      formSortOptionsDto,
      pageOptionsDto,
    ] as const,
    queryFn: async ({ pageParam }) => {
      const { data } = await apiService.get<
        PaginatedResponseDto<FormListResponseDto>
      >("/api/v1/forms", {
        params: {
          ...formFilterOptionsDto,
          ...formSortOptionsDto,
          ...pageOptionsDto,
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
  formSortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  useInfiniteQuery(
    getFetchFormPagesQueryOptions(
      formFilterOptionsDto,
      formSortOptionsDto,
      pageOptionsDto,
    ),
  );

export const prefetchFormPages = (
  queryClient: QueryClient,
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormPagesQueryOptions(
      formFilterOptionsDto,
      formSortOptionsDto,
      pageOptionsDto,
    ),
  );
