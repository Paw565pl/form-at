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

const getFetchMySubmissionOptions = (
  formIdOrSlug: string,
  userId: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>({
    queryKey: ["forms", formIdOrSlug, "submissions", { userId }],
    queryFn: async () => {
      const { data } = await apiService.get<SubmissionResponseDto>(
        `/api/v1/forms/${formIdOrSlug}/submissions/me`,
      );
      return data;
    },
    refetchOnWindowFocus: false,
    ...options,
  });

export const useFetchMySubmission = (
  formIdOrSlug: string,
  userId: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) => useQuery(getFetchMySubmissionOptions(formIdOrSlug, userId, options));

export const prefetchMySubmission = (
  queryClient: QueryClient,
  formIdOrSlug: string,
  userId: string,
  options?: Omit<
    UseQueryOptions<SubmissionResponseDto, AxiosError<ErrorResponseDto>>,
    "queryKey"
  >,
) =>
  queryClient.prefetchQuery(
    getFetchMySubmissionOptions(formIdOrSlug, userId, options),
  );
