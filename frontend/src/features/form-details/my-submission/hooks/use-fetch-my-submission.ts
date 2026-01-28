import { apiService } from "@/core/services/api-service";
import { ErrorResponseDto } from "@/core/types/error-response-dto";
import { SubmissionResponseDto } from "@/core/types/submission";
import {
  QueryClient,
  queryOptions,
  useQuery,
  UseQueryOptions,
} from "@tanstack/react-query";
import { AxiosError } from "axios";

export const getFetchMySubmissionOptions = (
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", formIdOrSlug, "submissions", "me"] as const,
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/submissions/me`,
      );
      return data;
    },
    staleTime: 1000 * 60 * 10, // 10 minutes
    ...options,
  });

export const useFetchMySubmission = (
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) => useQuery(getFetchMySubmissionOptions(formIdOrSlug, options));

export const prefetchMySubmission = (
  queryClient: QueryClient,
  formIdOrSlug: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryClient.prefetchQuery(getFetchMySubmissionOptions(formIdOrSlug, options));
