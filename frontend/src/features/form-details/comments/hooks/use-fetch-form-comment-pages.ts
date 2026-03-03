import { apiService } from "@/core/services/api-service";
import { CommentResponseDto } from "@/core/types/comment";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { PaginatedResponseDto } from "@/core/types/paginated-response-dto";
import {
  infiniteQueryOptions,
  QueryClient,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchFormCommentPagesQueryOptions = (formIdOrSlug: string) =>
  infiniteQueryOptions<
    PaginatedResponseDto<CommentResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: ["forms", formIdOrSlug, "comments"],
    queryFn: async ({ pageParam }) => {
      const { data } = await apiService.get<
        PaginatedResponseDto<CommentResponseDto>
      >(`/api/v1/forms/${formIdOrSlug}/comments`, {
        params: {
          page: pageParam,
        },
      });
      return data;
    },
    initialPageParam: 0,
    getNextPageParam: ({ page }) =>
      page.number + 1 < page.totalPages ? page.number + 1 : undefined,
  });

export const useFetchFormCommentPages = (formIdOrSlug: string) =>
  useInfiniteQuery(getFetchFormCommentPagesQueryOptions(formIdOrSlug));

export const prefetchFormCommentPages = (
  queryClient: QueryClient,
  formIdOrSlug: string,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormCommentPagesQueryOptions(formIdOrSlug),
  );
