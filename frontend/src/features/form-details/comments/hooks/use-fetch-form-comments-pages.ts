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

export const getFetchFormCommentsPagesQueryOptions = (formIdOrSlug: string) =>
  infiniteQueryOptions<
    PaginatedResponseDto<CommentResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: ["forms", formIdOrSlug, "comments"] as const,
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
    staleTime: 1000 * 60 * 10, // 10 minutes
  });

export const useFetchFormCommentsPages = (formIdOrSlug: string) =>
  useInfiniteQuery(getFetchFormCommentsPagesQueryOptions(formIdOrSlug));

export const prefetchFormCommentsPages = (
  queryClient: QueryClient,
  formIdOrSlug: string,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchFormCommentsPagesQueryOptions(formIdOrSlug),
  );
