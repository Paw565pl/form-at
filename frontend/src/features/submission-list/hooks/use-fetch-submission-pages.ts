import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { PageOptionsDto } from "@/core/types/page-options-dto";
import { PaginatedResponseDto } from "@/core/types/paginated-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import {
  infiniteQueryOptions,
  QueryClient,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

const getFetchSubmissionPagesQueryOptions = (
  formIdOrSlug: string,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  infiniteQueryOptions<
    PaginatedResponseDto<SubmissionResponseDto>,
    AxiosError<ErrorResponseDto>
  >({
    queryKey: ["forms", formIdOrSlug, "submissions", pageOptionsDto] as const,
    queryFn: async ({ pageParam }) => {
      const { data } = await apiService.get<
        PaginatedResponseDto<SubmissionResponseDto>
      >(`/api/v1/forms/${formIdOrSlug}/submissions`, {
        params: {
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

export const useFetchSubmissionPages = (
  formIdOrSlug: string,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  useInfiniteQuery(
    getFetchSubmissionPagesQueryOptions(formIdOrSlug, pageOptionsDto),
  );

export const prefetchSubmissionPages = (
  queryClient: QueryClient,
  formIdOrSlug: string,
  pageOptionsDto?: Omit<PageOptionsDto, "page">,
) =>
  queryClient.prefetchInfiniteQuery(
    getFetchSubmissionPagesQueryOptions(formIdOrSlug, pageOptionsDto),
  );
