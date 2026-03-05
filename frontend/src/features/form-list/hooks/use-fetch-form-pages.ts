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
  filterOptionsDto?: FormFilterOptionsDto,
  sortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  infiniteQueryOptions<
    PaginatedResponseDto<FormListResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: [
      "forms",
      filterOptionsDto ?? null,
      sortOptionsDto ?? null,
      pageOptionsDto ?? null,
    ],
    queryFn: async ({ pageParam }) => {
      const { data } = await apiService.get<
        PaginatedResponseDto<FormListResponseDto>
      >("/api/v1/forms", {
        params: {
          ...filterOptionsDto,
          ...sortOptionsDto,
          ...pageOptionsDto,
          page: pageParam,
        },
      });
      return data;
    },
    initialPageParam: 0,
    getNextPageParam: ({ page }) =>
      page.number + 1 < page.totalPages ? page.number + 1 : undefined,
  });

export const useFetchFormPages = (
  filterOptionsDto?: FormFilterOptionsDto,
  sortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  useInfiniteQuery(
    getFetchFormPagesQueryOptions(
      filterOptionsDto,
      sortOptionsDto,
      pageOptionsDto,
    ),
  );

export const prefetchFormPages = (
  queryClient: QueryClient,
  filterOptionsDto?: FormFilterOptionsDto,
  sortOptionsDto?: SortOptionsDto<FormSortOption>,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormPagesQueryOptions(
      filterOptionsDto,
      sortOptionsDto,
      pageOptionsDto,
    ),
  );
