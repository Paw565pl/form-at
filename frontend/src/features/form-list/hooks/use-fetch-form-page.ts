import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import {
  FormFilterOptionsDto,
  FormListResponseDto,
  formSortOptions,
} from "@/core/types/form";
import { PaginatedResponseDto } from "@/core/types/paginated-response-dto";
import {
  infiniteQueryOptions,
  QueryClient,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchFormPageQueryOptions = (
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionKey?: keyof typeof formSortOptions,
) => {
  const formSortOptionsDto =
    formSortOptionKey && formSortOptions[formSortOptionKey];

  return infiniteQueryOptions<
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
          sort: formSortOptionsDto?.getSearchParamValue(),
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
};

export const useFetchFormPage = (
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsKey?: keyof typeof formSortOptions,
) =>
  useInfiniteQuery(
    getFetchFormPageQueryOptions(formFilterOptionsDto, formSortOptionsKey),
  );

export const prefetchFormPage = (
  queryClient: QueryClient,
  formFilterOptionsDto?: FormFilterOptionsDto,
  formSortOptionsKey?: keyof typeof formSortOptions,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormPageQueryOptions(formFilterOptionsDto, formSortOptionsKey),
  );
